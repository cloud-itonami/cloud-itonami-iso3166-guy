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

## Official surface (curl/OCR-verified 2026-07-22)

- Procurement: the National Procurement and Tender Administration
  (NPTA, `www.npta.gov.gy` -- note `nptab.gov.gy` does NOT resolve in
  DNS), an agency established under the Minister of Finance and managed
  by its 7-member National Board, established/governed by the
  Procurement Act 2003 (Cap. 73:05) s.16. Popularly branded together as
  the National Procurement and Tender Administration Board (NPTAB, per
  the site's own footer copyright line).
- Register of Bidders: every supplier/contractor must submit an
  Application for Registration and become a Registered Bidder AT LEAST
  SEVEN DAYS before submitting a bid (Procurement (Amendment) Act 2019
  s.4A(2), independently corroborated by the Procurement (Register of
  Bidders) Regulations 2022 reg.5(2)).
- Business registration: the Registrar of Companies administers the
  Companies Act (Cap. 89:01) -- s.4 incorporation, s.8 certificate of
  incorporation (conclusive proof, company exists from the date shown
  on it). Which Ministry supervises the Registrar could not be
  independently confirmed this iteration (the apparent registrar
  subdomain, `dcra.gov.gy` -> `dcraguyana.org`, is behind a Cloudflare
  bot challenge this iteration did not attempt to bypass) -- an honest
  gap, not a guess.
- Tax: the Guyana Revenue Authority (GRA, `gra.gov.gy`) issues a
  Taxpayer Identification Number (TIN) via a SEPARATE application from
  business registration -- GRA's own TIN guidance requires the
  Commercial Registry Business Registration Certificate as a
  prerequisite supporting document for a Trade/Business TIN
  application, a two-act model (like ATG/GRD/BRB, unlike DMA's
  automatic same-transaction model).
- Local Content Act 2021 (Act No. 18 of 2021): genuinely investigated
  given Guyana's oil-and-gas boom, and cited in `src/statute/facts.cljc`
  as a real, currently topical general-compliance statute -- but
  deliberately NOT the flagship governor check here (see
  `docs/adr/0001-architecture.md` for why its two candidate mechanics
  were each set aside as too close to an already-used sibling
  mechanic).

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD check | `registration-lead-time-insufficient` (Register of Bidders MINIMUM seven-day lead time before bidding, Procurement (Amendment) Act 2019 s.4A(2) + Procurement (Register of Bidders) Regulations 2022 reg.5(2) -- see `docs/adr/0001-architecture.md`) |
| Compliance catalog | `src/statute/facts.cljc` -- Companies Act (Cap. 89:01), Labour Act (Cap. 98:01), Termination of Employment and Severance Pay Act (Cap. 96:01), Local Content Act 2021 |
| Tests | `clojure -M:dev:test` |
| Demo | `clojure -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

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
  Register of Bidders), not the Local Content Act 2021's own
  petroleum-sector-specific Local Content Master Plan / Annual Plan /
  Secretariat-certification regime -- a distinct, larger regulatory
  domain out of scope here. See `docs/adr/0001-architecture.md`.

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

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
