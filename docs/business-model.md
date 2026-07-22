# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Guyana

## Classification

- Repository: `cloud-itonami-iso3166-guy`
- ISO 3166: `GUY` (Co-operative Republic of Guyana)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Guyana public
  contract
- a foreign SME or civic-tech vendor entering the public sector in
  Guyana for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation
  and now needs public-sector market access

## Offer

- registration/submission walkthrough for public procurement under the
  Procurement Act 2003 (Cap. 73:05), including the mandatory Register
  of Bidders lead time (Procurement (Amendment) Act 2019 s.4A(2) +
  Procurement (Register of Bidders) Regulations 2022 reg.5(2)): a
  supplier or contractor must become a Registered Bidder AT LEAST SEVEN
  DAYS before submitting a bid
- business/tax registration checklist: Certificate of Incorporation
  from the Registrar of Companies (Companies Act Cap. 89:01), plus a
  SEPARATE Taxpayer Identification Number (TIN) application to the
  Guyana Revenue Authority (GRA) -- the Commercial Registry Business
  Registration Certificate is itself a prerequisite supporting document
  for the TIN application
- registration-lead-time-sufficiency screening: independent
  verification that an engagement's own declared bidder-registration
  date actually clears the mandatory seven-day lead time before its own
  declared bid-submission date, before any filing submission
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone -- it must be corrected
  against a cited official source first
- a bidder-registration date that falls short of the mandatory
  seven-day Register of Bidders lead time (Procurement (Amendment) Act
  2019 s.4A(2) / Procurement (Register of Bidders) Regulations 2022
  reg.5(2)) is a HARD hold on `:filing/submit`, independently
  recomputed rather than trusted from a self-reported registration date
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Guyana-licensed counsel or a registered agent
- this service is **not** a Local Content Act 2021 compliance system for
  petroleum-sector operators -- that Act's own Local Content Master
  Plan / Annual Plan / Secretariat-certification regime is a separate,
  larger regulatory domain, out of scope for this blueprint (see
  `docs/adr/0001-architecture.md`)

## Boundary with adjacent actors (read before forking)

- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) -- a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis -- the two compose (fork a COFOG-function
  blueprint AND this one to operate in Guyana).
