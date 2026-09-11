# cloud-itonami-iso3166-guy

Open ISO 3166 Blueprint for **GUY**: Co-operative Republic of Guyana --
**`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a Compliance Advisor +
independent **Market-Entry Compliance Governor** to navigate public-
procurement registration, local business/tax registration, and
regulatory-compliance rules in Guyana, so the operator can win and
service a government contract without hiring a full in-house
compliance department.

## Regulatory catalog (verified facts only)

Every fact in `src/marketentry/facts.kotoba` traces to one of the
official/primary sources below. See that namespace's docstring for the
full source list and the "explicitly NOT claimed" fabrication traps
this repository deliberately avoids (a previous draft of this
repository did NOT avoid all of them -- see `docs/adr/0001-
architecture.md` "Correction" section for what was found and fixed).

- **Business/company registration**: the Deeds and Commercial
  Registries Authority (DCRA), established by the Deeds and Commercial
  Registries Authority Act No. 4 of 2013, administers the Companies
  Act, the Business Names (Registration) Act, and the Partnership Act.
  A NON-RESIDENT company must register under the Companies Act 1991
  when "carrying on an undertaking" in Guyana -- triggered by ANY of:
  maintaining an office; maintaining a share transfer/registration
  office; entering two or more contracts with local parties for work
  performed in Guyana; appointing a resident agent; or owning/using
  profit-generating assets in Guyana. No online registration platform
  -- physical registration with the Registrar of Companies is required.
- **Public procurement**: the Procurement Act 2003 (Act No. 8 of
  2003), amended by the Procurement (Amendment) Act 2010 and the
  Procurement (Amendment) Act 2019. Regulations include the
  Procurement Regulations 2004, the Procurement Amendment Regulations
  2016, the Procurement Suspension and Debarment Regulations 2019, and
  Regulations No. 23 of 2022 (Register of Bidders). The administering
  body's CURRENT official self-branding is "National Procurement and
  Tender Administration (NPTA)" -- WITHOUT "Board" ("NPTAB" is an
  older/secondary-source name and is deliberately not used here) --
  established under Section 16(1) of the Procurement Act 2003. A new
  centralized e-procurement portal launched February 2026 at
  eprocure.gov.gy.
- **Public Procurement Commission (PPC)**: constitutional basis is
  Article 212W of the Constitution of Guyana; first Commissioners were
  sworn in 28 October 2016.
- **Tax registration**: the Guyana Revenue Authority (GRA) is the sole
  authority issuing Taxpayer Identification Numbers (TIN) -- required
  for anyone conducting business with a Government Department, Public
  Authority, Public Corporation, or the Bank of Guyana.
- **Local content**: the Local Content Act 2021 applies ONLY to
  persons engaged in petroleum operations/related activities under a
  license issued under the Petroleum Activities Act -- requires >=51%
  voting rights, >=75% executive/senior-management positions, and
  >=90% non-managerial staff held by Guyanese nationals, plus ~40
  reserved service categories. This is SECTOR-SPECIFIC (oil & gas
  only): outside oil & gas, there is no national-ownership requirement.

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD check | `business-registration-missing` (DCRA/Companies Act 1991 registration, CONDITIONAL on the non-resident "carrying on an undertaking" trigger set for a non-resident operator, unconditional for a resident one -- see `docs/adr/0001-architecture.md`) |
| Other checks | `evidence-incomplete` · `engagement-fee-mismatch` · `tin-unverified` (GRA TIN, unconditional) · `local-content-noncompliant` (Local Content Act 2021, SECTOR-CONDITIONAL, fires only for `:sector :petroleum`) |
| Compliance catalog | `src/statute/facts.kotoba` -- Companies Act (Cap. 89:01), Labour Act (Cap. 98:01), Termination of Employment and Severance Pay Act (Cap. 96:01), Local Content Act 2021 |
| Tests | `kbb -M:dev:test` |
| Demo | `kbb -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

## Actuation: `filing/draft` and `filing/submit` are always human-gated

`marketentry.registry/register-draft` and `register-submit` build an
**unsigned, non-authoritative record** of what a market-entry operator
*intends* to file -- neither function, nor anything in this actor,
ever calls a real NPTA/DCRA/GRA system. Whether that record is ever
turned into a real portal submission is entirely the human market-entry
operator's decision, enforced by two independent layers that must BOTH
agree before a real-world act happens:

1. **`marketentry.governor/high-stakes`** marks
   `:actuation/draft-filing` and `:actuation/submit-filing` as
   high-stakes -- any proposal carrying either `:stake` value always
   `:escalate?`s, regardless of confidence or how clean the governor's
   other checks are.
2. **`marketentry.phase/phases`** never puts `:filing/draft` or
   `:filing/submit` in any phase's `:auto` set (see the explicit
   comment in `phase.cljc`: "a permanent structural fact, not a rollout
   milestone still to come").

Concretely, in `marketentry.operation/build`'s StateGraph,
`interrupt-before #{:request-approval}` means the graph run itself
*pauses* (checkpointed, resumable) the moment a `:filing/draft` or
`:filing/submit` proposal reaches that node -- there is no code path
that reaches `:commit` for either op without a human explicitly
resuming the run with `{:approval {:status :approved :by "<human>"}}`.
`test/marketentry/governor_contract_test.kotoba`'s
`filing-draft-and-submit-never-auto-commit` test asserts this
end-to-end, at phase 3 (the most permissive phase).

## No robotics premise -- digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) -- the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set -- it always requires human sign-off.

## What this is NOT

- **Not the government of Guyana.** This blueprint is an independent
  operator the government contracts with or that bids into its
  procurement -- never the government itself, and never an official
  channel.
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Guyana-licensed counsel or
  a registered agent where the law requires licensed representation.
- **Not a Local Content Act compliance system.** This blueprint models
  general public-procurement market entry (business/tax registration +
  Register of Bidders awareness) plus a SECTOR-CONDITIONAL Local
  Content Act 2021 governor check for petroleum-sector engagements --
  not the Act's own Local Content Master Plan / Annual Plan /
  Secretariat-certification regime, a distinct, larger regulatory
  domain out of scope here.

## Capability layer

Required capabilities (`blueprint.edn`):

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Guyana:

- `src/culture/facts.kotoba` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
