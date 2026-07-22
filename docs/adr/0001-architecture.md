# ADR-0001: Architecture — Guyana market-entry compliance actor (`marketentry`)

**Status**: accepted (amended -- see "Correction" below)
**Date**: 2026-07-22 (original) / 2026-07-23 (correction)

## Context

`cloud-itonami-iso3166-guy` was published carrying ONLY `src/culture/`
(a regional-culture catalog, Wave 1) -- its
`:public-sector/market-entry-compliance` domain, declared in
`blueprint.edn`, was unimplemented (no `src/marketentry` / `src/statute`
at all). This ADR closes that gap, following the pattern established by
`cloud-itonami-iso3166-jpn` (origin) and the wider iso3166 family.

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
the family's harness verbatim (StateGraph node names, governor
hard/escalate contract, phase 0-3 rollout, `Store` protocol with
MemStore + DatomicStore parity via `kotoba-lang/langchain-store`) with
Guyana's own real market-entry rules for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test. DatomicStore uses `langchain-store.core`
  (`ls/enc`/`ls/dec*`/`ls/read-stream`/`ls/append-blob!`) instead of a
  hand-rolled EDN-blob codec.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`GUY-DFT-000000`, `GUY-SUB-000000`), plus the flagship
  business-registration-conditionality recompute (see below).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

## Correction (2026-07-23): a fabrication-risk audit found and fixed two problems in the original build

An earlier iteration of this repository (commit `147809e`) implemented
`marketentry.*` with a genuinely thorough-*looking* research process
(described curl/`pdftotext`/OCR extraction steps, "verbatim" statutory
quotes with specific section numbers) but produced content that could
not be independently reconciled against a separately-verified fact set
for Guyana's regulatory regime. Two specific problems were found and
corrected:

1. **The flagship governor check
   (`registration-lead-time-insufficient`) was built on an unverified,
   specific numeric claim**: a "seven-day" Register-of-Bidders minimum
   lead time attributed to "Procurement (Amendment) Act 2019 s.4A(2)"
   and "Procurement (Register of Bidders) Regulations 2022 reg.5(2)",
   including OCR'd "verbatim" quotes of both. The independently
   verified fact set for this task confirms Regulations No. 23 of 2022
   (Register of Bidders) EXISTS, but does not confirm this specific
   seven-day figure or these specific section/regulation numbers. Per
   this repository's own zero-fabrication discipline (never state a
   statutory specific that cannot be independently traced), this
   flagship check -- and the pure-function day-count arithmetic
   (`days-from-civil`, `earliest-eligible-bid-day`,
   `bidder-registration-lead-time-insufficient?`) it was built on --
   was REMOVED, not merely re-labeled.
2. **`:owner-authority` used "NPTAB" ("National Procurement and Tender
   Administration Board") as the procurement regulator's name.** The
   independently verified fact set is explicit that the CURRENT
   official self-branding is "National Procurement and Tender
   Administration (NPTA)" -- WITHOUT "Board" -- and that "NPTAB" is an
   older/secondary-source name. This was corrected fleet-wide across
   this repo's `facts.cljc`, `README.md`, `docs/business-model.md`, and
   `docs/operator-guide.md`.
3. **`marketentry.store`'s `DatomicStore` hand-rolled its own
   `enc`/`dec*` EDN-blob codec** -- the exact anti-pattern
   ADR-2607141600 identified as duplicated ~190 times fleet-wide and
   already fixed via `kotoba-lang/langchain-store`. Corrected to
   `require [langchain-store.core :as ls]` and use `ls/enc` / `ls/dec*`
   / `ls/read-stream` / `ls/append-blob!`, matching the pattern already
   in use by `cloud-itonami-iso3166-ago` / `-grc`.

### New flagship HARD check: `business-registration-missing` -- DCRA + the Companies Act 1991 non-resident trigger set

Replacing the removed lead-time check, this jurisdiction's flagship
check is now grounded entirely in the independently-verified fact set:
the Deeds and Commercial Registries Authority Act No. 4 of 2013
establishes DCRA, which administers the Companies Act. A NON-RESIDENT
company is required to register under the Companies Act 1991 only when
"carrying on an undertaking" in Guyana -- a five-way trigger set
(maintaining an office; maintaining a share transfer/registration
office; entering two or more contracts with local parties for work
performed in Guyana; appointing a resident agent; or owning/using
profit-generating assets in Guyana). A RESIDENT Guyanese entity has no
such conditionality; registration is always required.

`marketentry.registry/business-registration-missing?` independently
recomputes this: unconditional HARD violation for a resident entity
lacking `:business-registration-verified?`; CONDITIONAL for a
non-resident entity on whether ANY trigger in
`non-resident-undertaking-triggers` is present in the engagement's own
`:undertaking-triggers` set. A non-resident engagement that trips no
trigger is honestly NOT held even while unregistered -- this
conditionality (tested by `eng-8` in `marketentry.store/demo-data` and
the `business-registration-missing-never-fires-when-no-undertaking-
trigger-tripped` governor contract test) is itself the genuinely novel
check shape this jurisdiction adds, mirroring the discipline every
sibling actor's own flagship check applies of proving the check does
NOT fire when its own precondition is absent.

### Local Content Act 2021: kept sector-conditional, not force-fit into an unconditional check

The independently verified fact set is explicit that the Local Content
Act 2021 applies ONLY to persons engaged in petroleum operations/
related activities under a Petroleum Activities Act license --
requiring >=51% voting rights, >=75% executive/senior-management
positions, and >=90% non-managerial staff held by Guyanese nationals,
plus ~40 reserved service categories, and that OUTSIDE oil & gas there
is no national-ownership requirement at all.

`marketentry.registry/local-content-act-applies?` /
`local-content-noncompliant?` encode this conditionality directly:
`local-content-act-applies?` returns true ONLY when an engagement's own
`:sector` is `:petroleum`; `local-content-noncompliant?` is `false` for
every other sector regardless of `:local-content-compliant?`. The
governor's `local-content-noncompliant-violations` check is evaluated
for every `:filing/submit`, but only ever produces a violation for a
petroleum-sector engagement. `eng-6` (petroleum, noncompliant -> HOLD)
and `eng-7` (general sector, same "noncompliant" flag, -> NOT held)
in `marketentry.store/demo-data`, plus the corresponding governor
contract tests, prove BOTH directions of this conditionality
end-to-end -- this task's own explicit non-negotiable ("Local-Content-
Act check only fires for petroleum-sector engagements and never for
others").

### Other HARD checks (all unoverridable)

1. **spec-basis** -- never invent a jurisdiction's market-entry
   requirements (`marketentry.facts` catalog: NPTA / DCRA / GRA / PPC
   for GUY).
2. **evidence-incomplete** -- draft/submit require a full assessment
   checklist on file (business registration record, GRA TIN record,
   NPTA Register of Bidders application record, authorized-
   representative record).
3. **business-registration-missing** -- see above (FLAGSHIP).
4. **engagement-fee-mismatch** -- recompute `base-fee + monthly-rate ×
   monitoring-months` (ground-truth-recompute discipline, generic
   across the fleet).
5. **tin-unverified** -- UNCONDITIONAL (every engagement this actor
   handles is, by definition, conducting business with a Government
   Department/Public Authority/Public Corporation, which the Guyana
   Revenue Authority's own published guidance makes TIN-mandatory
   for -- so this check is not gated behind a `:requires-tin?` flag,
   unlike some sibling actors' conditional TIN checks).
6. **local-content-noncompliant** -- see above (SECTOR-CONDITIONAL).
7. **already-drafted / already-submitted** -- dedicated booleans, never
   a `:status` value.

### What was NOT touched

- `src/culture/facts.cljc` (Wave 1, unrelated batch) is untouched.
- `src/statute/facts.cljc` (the general-compliance catalog: Companies
  Act, Labour Act, Termination of Employment and Severance Pay Act,
  Local Content Act 2021) is untouched -- its own citations (mola.gov.gy
  / parliament.gov.gy URLs) were not part of this task's verified-facts
  review scope, and this ADR does not assert they are or are not
  independently confirmed.
- `marketentry.phase` and `marketentry.operation` needed no changes --
  the human-gated-actuation architecture (`interrupt-before
  #{:request-approval}`, `:filing/draft`/`:filing/submit` permanently
  absent from every phase's `:auto` set) was already correct and is
  unrelated to the factual-content issues above.

## Consequences

- `marketentry.facts` now states only independently-traceable facts,
  each attributable to a specific official source URL, with an explicit
  "explicitly NOT claimed" section listing the specific fabrication
  traps already identified and avoided (no invented "Procurement
  (Amendment) Act 2016"; "NPTA" never "NPTAB"; no invented registration
  fee figure; no invented Income Tax Act section number for TIN; the
  Local Content Act's staffing percentages never applied outside
  petroleum; Go-Invest never treated as a registration authority).
- The flagship check changed shape (from a date-arithmetic minimum-
  lead-time recompute to a conditional-registration-requirement
  recompute) -- both are genuinely different check OBJECTS from every
  other iso3166 sibling's own flagship, so this correction does not
  reduce the check's novelty, only its factual grounding.
- Sibling country blueprints can continue forking this family; this
  ADR is itself further evidence that a flagship check's underlying
  facts must be traceable to an independently-verified source before
  being encoded as governor logic that a human is asked to trust as a
  HARD, unoverridable hold.
