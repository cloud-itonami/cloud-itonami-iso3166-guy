(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `business-registration-missing?` grounds this jurisdiction's flagship
  governor check: the Companies Act 1991 requires a NON-RESIDENT
  company to register with the Registrar of Companies (via the Deeds
  and Commercial Registries Authority, DCRA) only when it is 'carrying
  on an undertaking' in Guyana -- a five-way trigger set (maintaining
  an office; maintaining a share transfer/registration office; entering
  two or more contracts with local parties for work performed in
  Guyana; appointing a resident agent; or owning/using
  profit-generating assets in Guyana). A RESIDENT Guyanese entity has no
  such conditionality -- registration is always required. This is a
  genuinely CONDITIONAL check (like every sibling's own conditional
  slot) rather than a blanket 'always register' rule, source: Grant
  Thornton Guyana, 'Guyanese registration requirements for non-resident
  companies'.

  `local-content-act-applies?` / `local-content-noncompliant?` ground
  the SECTOR-CONDITIONAL Local Content Act 2021 check: this Act applies
  ONLY to persons engaged in petroleum operations/related activities
  under a license issued under the Petroleum Activities Act -- it must
  NEVER fire for a non-petroleum-sector engagement (see
  `marketentry.facts` catalog docstring).

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real National Procurement and Tender Administration
  system, no host date API. It builds the RECORD an operator would
  keep, not the act of submitting a portal registration itself (that is
  `marketentry.operation`'s `:filing/submit`, always human-gated -- see
  README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

;; --------------- Companies Act 1991 non-resident registration gate ---------------

(def non-resident-undertaking-triggers
  "Companies Act 1991 'carrying on an undertaking' triggers for a
  NON-RESIDENT company -- tripping ANY ONE of these means the company
  must register with the Registrar of Companies (via DCRA) before
  continuing to operate in Guyana. Source: Grant Thornton Guyana,
  'Guyanese registration requirements for non-resident companies'."
  #{:maintains-office?
    :maintains-share-transfer-office?
    :two-or-more-local-contracts?
    :appointed-resident-agent?
    :owns-or-uses-profit-generating-assets?})

(defn non-resident-registration-required?
  "Does `engagement` (a NON-RESIDENT operator, `:resident?` false/nil)
  trip ANY Companies Act 1991 'carrying on an undertaking' trigger
  (present as a truthy key in `:undertaking-triggers`)? A RESIDENT
  Guyanese entity is out of scope for this specific gate -- see
  `business-registration-missing?`."
  [{:keys [resident? undertaking-triggers]}]
  (boolean
   (and (not (true? resident?))
        (some (fn [trigger] (contains? undertaking-triggers trigger))
              non-resident-undertaking-triggers))))

(defn business-registration-missing?
  "TRUE when `engagement` is required to be registered with the
  Registrar of Companies (via DCRA) -- either because it is a RESIDENT
  Guyanese entity (registration is always required) or because it is a
  NON-RESIDENT entity that trips a Companies Act 1991 'carrying on an
  undertaking' trigger (`non-resident-registration-required?`) -- but
  `:business-registration-verified?` is not true. A NON-RESIDENT
  engagement that trips NO trigger is honestly NOT required to
  register yet, and this fn returns false for it even when
  `:business-registration-verified?` is false/missing."
  [{:keys [resident? business-registration-verified?] :as engagement}]
  (boolean
   (and (or (true? resident?)
            (non-resident-registration-required? engagement))
        (not (true? business-registration-verified?)))))

;; --------------- Local Content Act 2021 (sector-gated) ---------------

(def local-content-sectors
  "Sectors the Local Content Act 2021 applies to -- ONLY persons engaged
  in petroleum operations/related activities under a license issued
  under the Petroleum Activities Act. Every other sector is OUT OF
  SCOPE for this Act -- see `marketentry.facts` catalog docstring."
  #{:petroleum})

(defn local-content-act-applies?
  "Does the Local Content Act 2021 apply to `engagement`'s own
  `:sector`? SECTOR-CONDITIONAL: true ONLY for petroleum-sector
  engagements, false for every other sector (including a missing/nil
  `:sector`) -- never fabricate a wider applicability."
  [{:keys [sector]}]
  (boolean (local-content-sectors sector)))

(defn local-content-noncompliant?
  "TRUE only when the Local Content Act 2021 actually applies to
  `engagement` (`local-content-act-applies?`) AND
  `:local-content-compliant?` is not true. For a non-petroleum-sector
  engagement this ALWAYS returns false, regardless of
  `:local-content-compliant?` -- the sector conditionality itself is
  the fact under test here."
  [{:keys [local-content-compliant?] :as engagement}]
  (boolean
   (and (local-content-act-applies? engagement)
        (not (true? local-content-compliant?)))))

;; ----------------------------- filing records -----------------------------

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
