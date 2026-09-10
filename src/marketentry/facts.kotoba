(ns marketentry.facts
  "Per-jurisdiction public-procurement market-entry regulatory catalog
  -- the G2-style spec-basis table the Market-Entry Compliance Governor
  checks every `:jurisdiction/assess` proposal against ('did the advisor
  cite an OFFICIAL public source for this jurisdiction's requirements,
  or did it invent one?').

  Guyana (GUY) catalog -- ZERO-FABRICATION discipline: every fact below
  traces to a specific, independently-checkable official/primary
  source. Nothing here states a fee figure, a section number, or a
  'verbatim' statutory quote that was not itself independently
  supplied and checked before this catalog was written -- an earlier
  draft of this repository (superseded, see `docs/adr/0001-
  architecture.md` 'Correction' section) DID cross that line (an
  invented seven-day Register-of-Bidders lead time attributed to a
  specific 's.4A(2)'/'reg.5(2)', and the popular-but-outdated
  'NPTAB' naming for the procurement regulator); this catalog was
  rewritten from a verified-facts-only source list to remove both.

  Verified facts (source URL on each field below):

  - Business/company registration: the Deeds and Commercial Registries
    Authority (DCRA), established by the Deeds and Commercial
    Registries Authority Act No. 4 of 2013, administers the Companies
    Act, the Business Names (Registration) Act, and the Partnership
    Act.
  - Foreign/external company registration: the Companies Act 1991
    requires a NON-RESIDENT company to register when 'carrying on an
    undertaking' in Guyana -- triggered by ANY of: maintaining an
    office; maintaining a share transfer/registration office; entering
    two or more contracts with local parties for work performed in
    Guyana; appointing a resident agent; or owning/using
    profit-generating assets in Guyana. There is no online registration
    platform -- physical registration with the Registrar of Companies
    is required.
  - Public procurement: the Procurement Act 2003 (Act No. 8 of 2003),
    amended by the Procurement (Amendment) Act 2010 and the Procurement
    (Amendment) Act 2019. Regulations include the Procurement
    Regulations 2004, the Procurement Amendment Regulations 2016, the
    Procurement Suspension and Debarment Regulations 2019, and
    Regulations No. 23 of 2022 (Register of Bidders).
  - The Public Procurement Commission (PPC)'s constitutional basis is
    Article 212W of the Constitution of Guyana; its first Commissioners
    were sworn in 28 October 2016.
  - The procurement administering body's CURRENT official self-branding
    is 'National Procurement and Tender Administration (NPTA)' --
    WITHOUT 'Board'. 'NPTAB' is an older/secondary-source name and is
    deliberately NOT used here. NPTA was established under Section
    16(1) of the Procurement Act 2003. A new centralized e-procurement
    portal launched February 2026 at eprocure.gov.gy.
  - Tax registration: the Guyana Revenue Authority (GRA) is the SOLE
    authority issuing Taxpayer Identification Numbers (TIN) -- required
    for anyone conducting business with a Government Department, Public
    Authority, Public Corporation, or the Bank of Guyana.
  - Local content: the Local Content Act 2021 applies ONLY to persons
    engaged in petroleum operations/related activities under a license
    issued under the Petroleum Activities Act -- requires >=51% voting
    rights, >=75% executive/senior-management positions, and >=90%
    non-managerial staff held by Guyanese nationals, plus ~40 reserved
    service categories. This is SECTOR-SPECIFIC (oil & gas only):
    outside oil & gas, investors are not required to source locally and
    there is no national-ownership requirement.

  Explicitly NOT claimed (fabrication traps already caught before this
  catalog was written -- see `docs/adr/0001-architecture.md`):

  - No 'Procurement (Amendment) Act 2016' -- no such act exists; only
    the 'Procurement Amendment Regulations 2016' (a regulation, not an
    act amendment).
  - The procurement body is 'NPTA', never 'NPTAB'.
  - No specific registration FEE figure (only found via secondary
    aggregators, not DCRA's own fee schedule -- omitted here).
  - No specific Income Tax Act section number for TIN registration (not
    found on GRA's own procedural pages -- omitted here).
  - The Local Content Act's 51%/75%/90% staffing requirements and
    ~40-reserved-category list apply ONLY to petroleum-sector
    engagements, never to a non-oil-and-gas engagement.
  - Go-Invest (Guyana Office for Investment) is an investor-facilitation
    / liaison body, NOT a registration authority -- actual registration
    still happens at DCRA.

  Coverage is reported HONESTLY (see `coverage`): a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the generic
  intake/portal-registration/filing evidence set; `:legal-basis` /
  `:owner-authority` / `:provenance` are the G2 citation the governor
  requires before any `:jurisdiction/assess` proposal can commit.

  For GUY: `:owner-authority` is NPTA (Procurement Act 2003 s.16(1));
  `:business-registration-*` is DCRA (a DIFFERENT body, DCRA Act No. 4
  of 2013) and carries the Companies Act 1991 non-resident 'carrying on
  an undertaking' trigger set that grounds
  `marketentry.registry/business-registration-missing?`;
  `:corporate-number-*` is GRA (TIN, a THIRD body); `:rep-*` is the
  Public Procurement Commission (Article 212W); `:local-content-*` is
  the Local Content Act 2021, deliberately marked
  `:local-content-sector` so the governor can enforce the SECTOR
  conditionality (petroleum-only) rather than applying it to every
  engagement."
  {"GUY" {:name "Guyana"
          :owner-authority "National Procurement and Tender Administration (NPTA)"
          :legal-basis "Procurement Act 2003 (Act No. 8 of 2003) s.16(1) (NPTA established), amended by the Procurement (Amendment) Act 2010 and the Procurement (Amendment) Act 2019"
          :national-spec "Register of Bidders registration under the Procurement Regulations 2004 / Procurement Amendment Regulations 2016 / Regulations No. 23 of 2022 (Register of Bidders); centralized e-procurement portal eprocure.gov.gy (launched February 2026)"
          :provenance "https://www.npta.gov.gy/procurement-documents/procurement-act-and-regulations/"
          :required-evidence ["Business registration record (Deeds and Commercial Registries Authority, DCRA)"
                              "Taxpayer Identification Number (TIN) record (Guyana Revenue Authority, GRA)"
                              "Register of Bidders application/registration record (National Procurement and Tender Administration, NPTA, per Regulations No. 23 of 2022)"
                              "Authorized-representative record"]
          :corporate-number-owner-authority "Guyana Revenue Authority (GRA)"
          :corporate-number-legal-basis "GRA is the sole authority issuing Taxpayer Identification Numbers (TIN) -- required for anyone conducting business with a Government Department, Public Authority, Public Corporation, or the Bank of Guyana"
          :corporate-number-provenance "https://gra.gov.gy/how-to-obtain-a-tin/"
          :business-registration-owner-authority "Deeds and Commercial Registries Authority (DCRA)"
          :business-registration-legal-basis "Deeds and Commercial Registries Authority Act No. 4 of 2013 (establishes DCRA; administers the Companies Act, the Business Names (Registration) Act, and the Partnership Act). A NON-RESIDENT company must register under the Companies Act 1991 when 'carrying on an undertaking' in Guyana -- triggered by ANY of: maintaining an office; maintaining a share transfer/registration office; entering two or more contracts with local parties for work performed in Guyana; appointing a resident agent; or owning/using profit-generating assets in Guyana. No online registration platform -- physical registration with the Registrar of Companies is required."
          :business-registration-provenance "https://dcra.gov.gy/ ; https://www.grantthornton.gy/publications/guyanese-registration-requirements-for-non-resident-companies/"
          :rep-owner-authority "Public Procurement Commission (PPC)"
          :rep-legal-basis "Constitutional basis: Article 212W of the Constitution of Guyana. First Commissioners sworn in 28 October 2016. The PPC is the oversight body a debarment/registry decision under the Procurement Act ultimately answers to."
          :rep-provenance "https://guyanachronicle.com/2016/04/29/the-public-procurement-commission/"
          :local-content-legal-basis "Local Content Act 2021 -- applies ONLY to persons engaged in petroleum operations/related activities under a license issued under the Petroleum Activities Act: requires >=51% voting rights, >=75% executive/senior-management positions, and >=90% non-managerial staff held by Guyanese nationals, plus ~40 reserved service categories. Outside oil & gas, investors are not required to source locally -- no national-ownership requirement applies."
          :local-content-provenance "https://petroleum.gov.gy/documents/local-content-act-2021 ; https://oilnow.gy/featured/a-look-at-guyanas-local-content-requirements/"
          :local-content-sector :petroleum}
   "USA" {:name "United States"
          :owner-authority "U.S. General Services Administration (GSA) / SAM.gov"
          :legal-basis "Federal Acquisition Regulation (FAR); System for Award Management"
          :national-spec "SAM.gov entity registration + NAICS self-certification"
          :provenance "https://sam.gov/"
          :required-evidence ["EIN record"
                              "SAM.gov registration record"
                              "State business registration record"
                              "Authorized-representative record"]}
   "DEU" {:name "Germany"
          :owner-authority "Beschaffungsamt des BMI / e-Vergabe platforms"
          :legal-basis "Gesetz gegen Wettbewerbsbeschränkungen (GWB) / VgV"
          :national-spec "e-Vergabe supplier registration under EU procurement directives"
          :provenance "https://www.evergabe-online.de/"
          :required-evidence ["Handelsregister extract"
                              "e-Vergabe registration record"
                              "USt-IdNr record"
                              "Authorized-representative record"]}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to assess or file
  on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-guy R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog for market-entry navigation, "
                 "not a survey of all ~194 jurisdictions -- extend "
                 "`marketentry.facts/catalog`, never fabricate a "
                 "jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))

(defn corporate-number-spec-basis
  "The jurisdiction's corporate-number / tax-id regime (GRA TIN, for
  GUY), or nil."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority
                       :corporate-number-legal-basis
                       :corporate-number-provenance]))))

(defn business-registration-spec-basis
  "The jurisdiction's business/company (state) registration regime, or
  nil. Guyana's business-registration authority is DCRA -- a DIFFERENT
  body from both `:corporate-number-spec-basis` (GRA, tax) and
  `:owner-authority` (NPTA, procurement)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:business-registration-owner-authority sb)
      (select-keys sb [:business-registration-owner-authority
                       :business-registration-legal-basis
                       :business-registration-provenance]))))

(defn rep-spec-basis
  "The jurisdiction's procurement-oversight/debarment-basis regime
  (Public Procurement Commission, for GUY), or nil."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:rep-owner-authority sb)
      (select-keys sb [:rep-owner-authority :rep-legal-basis :rep-provenance]))))

(defn local-content-spec-basis
  "The jurisdiction's Local Content Act regime, or nil. Guyana's regime
  is SECTOR-GATED (`:local-content-sector`) -- this fn returns the
  spec-basis regardless of any particular engagement's sector; the
  SECTOR CONDITIONALITY itself (apply only when an engagement's own
  `:sector` matches `:local-content-sector`, never otherwise) is
  enforced by `marketentry.registry/local-content-act-applies?` and
  `marketentry.governor`, not here."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:local-content-legal-basis sb)
      (select-keys sb [:local-content-legal-basis :local-content-provenance
                       :local-content-sector]))))
