# Operator Guide

## First Deployment

1. Confirm the client's incorporation/legal-entity status is complete
   (route to `cloud-itonami-M6910` or local counsel first if not).
2. Register the client's intake: business type (resident vs.
   non-resident), sector (petroleum vs. general), target public
   function, prior filing history in Guyana if any.
3. Run the advisor in read-only mode against the Procurement Act 2003
   (Act No. 8 of 2003) requirements, administered by the National
   Procurement and Tender Administration (NPTA, s.16(1)).
4. Compare the checklist against the client's current documentation:
   - a business registration record from the Deeds and Commercial
     Registries Authority (DCRA) -- always required for a resident
     entity; for a NON-resident entity, required only once it trips a
     Companies Act 1991 "carrying on an undertaking" trigger
     (maintaining an office; maintaining a share transfer/registration
     office; entering two or more local contracts for Guyana-performed
     work; appointing a resident agent; or owning/using
     profit-generating assets in Guyana)
   - a separate Guyana Revenue Authority (GRA) Taxpayer Identification
     Number (TIN) application
   - Register of Bidders awareness (Regulations No. 23 of 2022)
   - for a petroleum-sector client only: Local Content Act 2021
     compliance (>=51% Guyanese-held voting rights, >=75%
     executive/senior-management and >=90% non-managerial staff
     Guyanese, ~40 reserved service categories) -- this step is SKIPPED
     entirely for a non-petroleum-sector client
5. Enable gated filing-draft assistance once the Market-Entry Compliance
   Governor contract is trusted; actual submission always requires human
   sign-off.

## Minimum Production Controls

- client-owned data store for business/tax registration documents
- clear provenance (official portal/regulation citation) for every
  requirement surfaced
- approval workflow for any portal registration or filing submission
- independent re-verification that a non-resident client's own
  declared Companies Act 1991 trigger status actually requires DCRA
  business registration before any `:filing/submit` -- never trust a
  self-reported "registered" flag without checking whether registration
  was even required
- independent re-verification, for petroleum-sector clients only, of
  Local Content Act 2021 compliance before any `:filing/submit` -- and
  explicit confirmation that this step is skipped for every
  non-petroleum-sector client (never apply the Act's staffing/ownership
  percentages outside oil & gas)
- named referral relationship with Guyana-licensed counsel or a
  registered agent for anything beyond checklist/draft assistance
- monthly audit export
- disputes/appeals route to the Public Procurement Commission (Article
  212W of the Constitution of Guyana) as provided by the Procurement
  Act, not this actor -- it has no standing to file a complaint on the
  client's behalf

## Certification

Certified operators must prove data provenance, audit traceability, that
automated actions cannot bypass the Market-Entry Compliance Governor, and
a working referral relationship with Guyana-licensed counsel or a
registered agent for whatever licensed representation the law of
Guyana requires for actual public-procurement filings.
