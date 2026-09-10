(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Guyanese procurement law, whether a non-resident operator
  has actually tripped the Companies Act 1991 'carrying on an
  undertaking' registration trigger, whether a claimed engagement fee
  actually equals base + months x rate, whether a Taxpayer
  Identification Number (TIN) has been verified, whether the Local
  Content Act 2021 even applies to this engagement's sector, or when a
  draft stops being a draft and becomes a real-world npta.gov.gy
  Register-of-Bidders submission, so this MUST be a separate system
  able to *reject* a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Six checks (items 1-6 below), in priority order, ALL HARD
  violations: a human approver CANNOT override them. Item 7
  (confidence/actuation gate) is SOFT: it asks a human to look (low
  confidence / actuation), and the human may approve -- but see
  `marketentry.phase`: for `:stake :actuation/draft-filing`/
  `:actuation/submit-filing` NO phase ever allows auto-commit either.
  Two independent layers agree that actuation is always a human call.

  Of the six HARD checks, THREE are this jurisdiction's own regulatory
  content (this task's own 'Recommended check count: 5' review
  covered: business-registration/DCRA -> item 3 below;
  NPTA-procurement-registration + PPC-oversight awareness -> folded
  into items 1-2's citation/checklist requirement, since Guyana's
  verified facts give no additional independently-recomputable ground
  truth beyond 'was the official spec-basis cited and is the
  registration evidence on file'; GRA TIN -> item 5; Local Content Act
  2021 sector-conditional -> item 6); spec-basis/evidence-incomplete
  (items 1-2) and engagement-fee-mismatch (item 4) are the same
  universal structural guards every sibling actor in this fleet
  carries, not jurisdiction-specific regulatory content.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one? Also carries this
                                       vertical's DCRA (business
                                       registration)/NPTA (procurement
                                       registration)/GRA (tax)/PPC
                                       (procurement oversight, Article
                                       212W) citation requirement --
                                       see `evidence-incomplete` below.
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file (business
                                       registration record, GRA TIN
                                       record, NPTA Register of Bidders
                                       application record,
                                       authorized-representative
                                       record)?
    3. Business registration
       missing                       -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement is required to be
                                       registered with the Registrar of
                                       Companies via DCRA (always true
                                       for a resident Guyanese entity;
                                       CONDITIONAL for a non-resident
                                       entity on the Companies Act 1991
                                       'carrying on an undertaking'
                                       trigger set) and, if so, whether
                                       `:business-registration-
                                       verified?` is true. FLAGSHIP
                                       check for this jurisdiction --
                                       grounded in the Deeds and
                                       Commercial Registries Authority
                                       Act No. 4 of 2013 (DCRA) plus the
                                       Companies Act 1991 non-resident
                                       trigger set (see
                                       `marketentry.registry`), never
                                       fires for a non-resident
                                       engagement that trips no
                                       trigger.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. TIN unverified               -- for `:filing/submit`,
                                       INDEPENDENTLY check
                                       `:tin-verified?`, UNCONDITIONALLY
                                       (not gated behind a
                                       `:requires-tin?` flag): every
                                       engagement this actor exists for
                                       is, by definition, conducting
                                       business with a Government
                                       Department/Public Authority/
                                       Public Corporation, and the
                                       Guyana Revenue Authority (GRA)'s
                                       own published guidance makes a
                                       TIN mandatory for exactly that.
    6. Local Content Act
       noncompliant                  -- for `:filing/submit`,
                                       SECTOR-CONDITIONAL: fires ONLY
                                       when the engagement's own
                                       `:sector` is `:petroleum` (Local
                                       Content Act 2021 applies only to
                                       persons engaged in petroleum
                                       operations/related activities
                                       under a Petroleum Activities Act
                                       license) AND
                                       `:local-content-compliant?` is
                                       not true. NEVER fires for any
                                       other sector -- the sector
                                       conditionality itself is the
                                       fact under test (see
                                       `marketentry.registry/local-
                                       content-act-applies?`).
    7. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(DCRA事業者登録/GRA TIN登録/NPTA Register of Bidders登録/代理人確認等)が充足していない状態での提案"}]))))

(defn- business-registration-missing-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the engagement
  is required to be registered with the Registrar of Companies (via
  DCRA) -- unconditional for a resident entity, CONDITIONAL on the
  Companies Act 1991 'carrying on an undertaking' trigger set for a
  non-resident entity -- and, if so, whether it actually has been.
  FLAGSHIP check for this vertical."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/business-registration-missing? e)
        [{:rule :business-registration-missing
          :detail (str subject " は Deeds and Commercial Registries Authority (DCRA) への"
                      "事業者登録(Companies Act 1991)が未確認 -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- tin-unverified-violations
  "For `:filing/submit`, INDEPENDENTLY check `:tin-verified?`,
  UNCONDITIONALLY -- every engagement this actor handles is by
  definition conducting business with a Government Department/Public
  Authority/Public Corporation, and GRA's own published guidance makes
  a TIN mandatory for exactly that (never gated behind a
  `:requires-tin?` flag)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (true? (:tin-verified? e))
        [{:rule :tin-unverified
          :detail (str subject " は Guyana Revenue Authority (GRA) の"
                      "Taxpayer Identification Number (TIN) 確認が未完了 -- "
                      "提出提案は進められない")}]))))

(defn- local-content-noncompliant-violations
  "For `:filing/submit`, SECTOR-CONDITIONAL: only evaluates the Local
  Content Act 2021 when the engagement's own `:sector` is `:petroleum`
  -- never for any other sector."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/local-content-noncompliant? e)
        [{:rule :local-content-noncompliant
          :detail (str subject " は石油部門(petroleum operations)の案件であり、"
                      "Local Content Act 2021 の要件(Guyana国籍株主51%以上/"
                      "上級管理職75%以上/非管理職90%以上等、石油部門限定)への"
                      "適合が未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (business-registration-missing-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (tin-unverified-violations request st)
                           (local-content-noncompliant-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
