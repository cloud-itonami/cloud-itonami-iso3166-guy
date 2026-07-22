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

  `days-until-eligible-bid` / `bidder-registration-lead-time-insufficient?`
  are THIS vertical's own new ground-truth check, grounding GUY's
  flagship governor check
  (`marketentry.governor/registration-lead-time-insufficient-violations`):
  the Procurement (Amendment) Act 2019 s.4A(2) (own primary text, OCR'd
  directly from the Official Gazette PDF -- see `marketentry.facts`),
  independently corroborated by the Procurement (Register of Bidders)
  Regulations 2022 reg.5(2) (same primary-source discipline, a SECOND
  document), fixes a MINIMUM lead time: a supplier or contractor must
  submit an Application for Registration and become a Registered Bidder
  AT LEAST SEVEN DAYS before submitting a bid.

  This is a DIFFERENT check SHAPE from every prior sibling this repo
  mirrors: not a dual authority-escalation ladder over a procuring
  entity's own governance hierarchy (Dominica), not a backward-looking
  director/officer conviction-disqualification lookback (Grenada), not
  a closed-set validity test on a filing's own execution instrument
  (Estonia) -- and, MOST IMPORTANTLY, it is the temporal MIRROR IMAGE of
  Barbados's own flagship: BRB's `supplier-registration-expired?`
  recomputes a MAXIMUM validity window (registration must not be too
  OLD -- a stale/lapsed credential is the failure mode); GUY's
  `bidder-registration-lead-time-insufficient?` recomputes a MINIMUM
  lead time (registration must not be too RECENT -- a rushed,
  last-minute registration used to dodge the Administration's own
  seven-day review/publication window is the failure mode). Same
  general surface (date arithmetic against a declared registration
  date) but the opposite comparison direction and a genuinely different
  regulatory concern, grounded in Guyana's own two independent primary
  sources rather than copied from BRB's shape.

  Dates are plain ISO-8601 \"YYYY-MM-DD\" strings -- deliberately no
  external date/calendar library and no host date API (`java.time` /
  `js/Date`), so the recompute is byte-identical on every `.cljc`
  target, the same discipline BRB's `compute-registration-expiry` uses.
  BRB bumps only the 4-digit year prefix (sufficient for a whole-year
  validity window); GUY's window is measured in DAYS, so this namespace
  instead converts each ISO date to a day-count via Howard Hinnant's
  `days_from_civil` algorithm (http://howardhinnant.github.io/date_algorithms.html)
  -- pure integer arithmetic over the proleptic Gregorian calendar, no
  external library, correct across month/year boundaries and leap
  years, and independently cross-checked against Python's `datetime`
  for several dates (1970-01-01, 2000-02-29 and 2024-02-29 (leap days),
  2019-06-12, 2021-12-31, 2026-07-21) while writing this namespace.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real National Procurement and Tender Administration
  system. It builds the RECORD an operator would keep, not the act of
  submitting a portal registration itself (that is
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

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

;; ----------------------- pure ISO-8601 day-count arithmetic -----------------------

(defn- parse-int [s]
  #?(:clj (Integer/parseInt s)
     :cljs (js/parseInt s 10)))

(defn days-from-civil
  "Howard Hinnant's `days_from_civil` algorithm: converts a proleptic
  Gregorian calendar date (`y` `m` `d`, 1-indexed month/day) to an
  integer day count relative to 1970-01-01 (the Unix epoch). Pure
  integer arithmetic only -- no external date/calendar library, no
  host date API. Valid for `y` >= 0 (every real engagement date in this
  catalog is 20xx)."
  [y m d]
  (let [y'  (if (<= m 2) (dec y) y)
        era (quot y' 400)
        yoe (- y' (* era 400))
        doy (+ (quot (+ (* 153 (+ m (if (> m 2) -3 9))) 2) 5) (dec d))
        doe (+ (* yoe 365) (quot yoe 4) (- (quot yoe 100)) doy)]
    (+ (* era 146097) doe -719468)))

(defn parse-iso-date
  "\"YYYY-MM-DD\" -> integer day count since 1970-01-01, or nil for a
  blank/missing date. Does not validate calendar correctness beyond
  what `days-from-civil` computes (garbage-in/garbage-out is treated as
  the caller's problem, matching this family's minimal-recompute
  discipline)."
  [s]
  (when (and s (>= (count s) 10))
    (days-from-civil (parse-int (subs s 0 4))
                      (parse-int (subs s 5 7))
                      (parse-int (subs s 8 10)))))

(def registration-lead-time-days
  "Procurement (Amendment) Act 2019 s.4A(2) (inserted into the
  Procurement Act, Cap. 73:05) + Procurement (Register of Bidders)
  Regulations 2022 (Regulations No. 23 of 2022) reg.5(2): a supplier or
  contractor must be a Registered Bidder AT LEAST SEVEN DAYS before
  submitting a bid."
  7)

(defn earliest-eligible-bid-day
  "The ground-truth earliest day-count on which `bidder-registration-
  date` (\"YYYY-MM-DD\") is eligible to submit a bid -- registration
  day-count + `registration-lead-time-days`. nil if `bidder-
  registration-date` is missing/blank."
  [bidder-registration-date]
  (when-let [reg-day (parse-iso-date bidder-registration-date)]
    (+ reg-day registration-lead-time-days)))

(defn bidder-registration-lead-time-insufficient?
  "Does `engagement`'s own declared `:submission-date` fall STRICTLY
  BEFORE its own declared `:bidder-registration-date` +
  `registration-lead-time-days` -- i.e. was the bid submitted before
  the mandatory seven-day Register of Bidders lead time had elapsed?
  Missing either date is never treated as insufficient here (that is
  the `evidence-incomplete` check's job, upstream in the phase where an
  assessment must already exist)."
  [{:keys [bidder-registration-date submission-date]}]
  (boolean
   (when-let [earliest (earliest-eligible-bid-day bidder-registration-date)]
     (when-let [submission-day (parse-iso-date submission-date)]
       (< submission-day earliest)))))

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
