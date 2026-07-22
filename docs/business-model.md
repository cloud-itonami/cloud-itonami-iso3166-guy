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
- a petroleum-sector operator (Local Content Act 2021 applies)

## Offer

- registration/submission walkthrough for public procurement under the
  Procurement Act 2003 (Act No. 8 of 2003), administered by the
  National Procurement and Tender Administration (NPTA, established
  under s.16(1)) -- including Register of Bidders awareness
  (Regulations No. 23 of 2022)
- business/tax registration checklist: a business registration record
  from the Deeds and Commercial Registries Authority (DCRA, Deeds and
  Commercial Registries Authority Act No. 4 of 2013), plus a SEPARATE
  Taxpayer Identification Number (TIN) application to the Guyana
  Revenue Authority (GRA)
- non-resident registration screening: independent verification of
  whether a foreign/non-resident engagement has actually tripped a
  Companies Act 1991 "carrying on an undertaking" trigger (maintaining
  an office; maintaining a share transfer/registration office; entering
  two or more local contracts for Guyana-performed work; appointing a
  resident agent; or owning/using profit-generating assets in Guyana)
  before requiring DCRA registration -- and holding when it has
  tripped one but is not yet registered
- petroleum-sector Local Content Act 2021 screening: SECTOR-CONDITIONAL
  -- only evaluated for engagements whose own sector is petroleum, never
  applied to a non-oil-and-gas engagement
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
- a non-resident engagement that has tripped a Companies Act 1991
  "carrying on an undertaking" trigger but has no verified DCRA
  business registration is a HARD hold on `:filing/submit`,
  independently recomputed rather than trusted from a self-reported
  flag
- an unverified GRA Taxpayer Identification Number (TIN) is a HARD hold
  on `:filing/submit` -- unconditional, since every engagement this
  service exists for is, by definition, conducting business with a
  government body
- a petroleum-sector engagement that is not Local Content Act 2021
  compliant is a HARD hold on `:filing/submit` -- this check is
  SECTOR-CONDITIONAL and never applied to a non-petroleum engagement
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
