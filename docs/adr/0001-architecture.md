# ADR-0001: Architecture — Guyana market-entry compliance actor (`marketentry`)

**Status**: accepted
**Date**: 2026-07-22

## Context

`cloud-itonami-iso3166-guy` was published carrying ONLY `src/culture/`
(a regional-culture catalog, Wave 1) -- its
`:public-sector/market-entry-compliance` domain, declared in
`blueprint.edn`, was unimplemented (no `src/marketentry` / `src/statute`
at all). This ADR closes that gap, following the pattern established by
`cloud-itonami-iso3166-jpn` (origin) and the wider iso3166 family, most
recently studied against `cloud-itonami-iso3166-dma` / `-grd` / `-brb` /
`-est` (cloned fresh into a scratch workspace and read in full before
writing anything here, per this task's own instruction).

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
the family's harness verbatim (StateGraph node names, governor
hard/escalate contract, phase 0-3 rollout, `Store` protocol with
MemStore + DatomicStore parity) and researching Guyana's own real
market-entry rules from scratch for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`GUY-DFT-000000`, `GUY-SUB-000000`), plus the flagship
  minimum-lead-time date recompute (see below).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

### `nptab.gov.gy` does not resolve -- the real domain is `www.npta.gov.gy`

The task's own brief named `nptab.gov.gy`. `dig`/`nslookup` against both
the local resolver and Google's public resolver (8.8.8.8) returned
NXDOMAIN for `nptab.gov.gy` and every other guessed variant
(`www.nptab.gov.gy`, `nptaboard.gov.gy`, `nptab.org.gy`). The real,
live site was found by first fetching `finance.gov.gy` (Ministry of
Finance) and following its own agency directory link to
`finance.gov.gy/about-us-2/agencies/national-procurement-and-tender-administration/`,
whose own text states: "The National Procurement and Tender
Administration was established in accordance with Section 16 (1) of
the Procurement Act 2003 which came into effect in November 2004, with
the signing of the Order by the Minister of Finance." That page names
the website: `http://www.npta.gov.gy`, confirmed live (curl 200,
current 2026-dated contract-award records). The Procurement Act 2003
itself (Cap. 73:05, a genuine text-layer PDF downloaded from
`npta.gov.gy/wp-content/uploads/Procurement-Act-2003.pdf` and read
directly via `pdftotext -layout`) s.16(1)-(2) confirms: "There is
hereby established an agency reporting to the Minister of Finance ...
to be known as the National Procurement and Tender Administration. The
Administration shall be managed by the National Board which shall
consist of seven members ..." -- and s.2(i) separately defines
"'National Board' means the National Procurement and Tender Board
established under section 16." The live site's own footer reads
"(c) 2021 National Procurement and Tender Administration Board" --
confirming the task brief's "NPTAB" is the popular/site name for the
combination of the Administration and its National Board, even though
the Act's own text names the two components (agency vs. board)
separately. `marketentry.facts`'s `:owner-authority` states both names
honestly.

### Flagship HARD check: `registration-lead-time-insufficient` -- a check OBJECT genuinely different from every prior sibling, and the temporal MIRROR IMAGE of BRB's

The Procurement (Amendment) Act 2019 (Act No. 14 of 2019, passed by the
National Assembly 15 May 2019, gazetted 12 June 2019 -- downloaded
directly from `npta.gov.gy/wp-content/uploads/Procurement-Amendment-Act-2019.pdf`;
this PDF has a genuine text layer on its cover/TOC pages but the actual
amendment body (pages 3-5) is scanned-image-only -- `pdftotext -f 3 -l 3`
returned only header/footer text, confirmed by rendering the same page
to a PNG with `pdftoppm -r 300` and OCR'ing it directly with
`tesseract`) inserts a new s.4A into the Procurement Act. s.4A(2), OCR'd
verbatim: "Every supplier or contractor shall apply to be registered as
a bidder in the register of bidders in order to participate in
procurement proceedings at least seven days before taking part in any
procurement proceedings." This is INDEPENDENTLY corroborated -- a
genuinely SECOND primary source, not a paraphrase of the first -- by the
Procurement (Register of Bidders) Regulations 2022 (Regulations No. 23
of 2022, made under Procurement Act s.61 with the advice of the
National Procurement and Tender Board -- downloaded from
`npta.gov.gy/wp-content/uploads/Regulations-No.-23-of-2022-....pdf`,
entirely scanned-image-only across all 15 pages, `pdftoppm -r 300` +
`tesseract` OCR'd directly) reg.5(2), OCR'd verbatim: "All suppliers and
contractors who are interested in participating in procurement governed
by the Act shall submit an Application for Registration as set out in
the Schedule, at least seven days before submitting a bid for a
procurement contract for which they are interested in bidding."

`marketentry.registry/earliest-eligible-bid-day` independently
recomputes the earliest day a bidder-registration date makes a bid
submission eligible (registration day-count + 7), and
`bidder-registration-lead-time-insufficient?` HARD-holds
`:filing/submit` if the engagement's own declared `:submission-date`
falls strictly before that day. Because the underlying unit is DAYS (not
whole years), this namespace implements Howard Hinnant's
`days_from_civil` proleptic-Gregorian-calendar algorithm in pure integer
arithmetic (no external date library, no host date API -- the same
`.cljc`-portability discipline BRB's own `compute-registration-expiry`
uses for its year-level recompute) and cross-checks it against Python's
`datetime` for several known dates (including two leap days) while
writing the namespace; those exact cross-checked values are asserted
directly in `test/marketentry/registry_test.clj`.

This is a check OBJECT genuinely different from every prior iso3166
sibling this repo mirrors, and specifically the temporal MIRROR IMAGE of
Barbados's own flagship: BRB's `supplier-registration-expired?`
recomputes a MAXIMUM validity window (a registration that is too OLD --
lapsed/stale -- is the failure mode, 3 years from the Public Procurement
Act 2021 s.86(5)); GUY's `bidder-registration-lead-time-insufficient?`
recomputes a MINIMUM lead time (a registration that is too RECENT --
rushed at the last minute to dodge the Administration's own review
window -- is the failure mode, 7 days from the Procurement (Amendment)
Act 2019 s.4A(2)/Regulations reg.5(2)). Both share the general MECHANIC
("date arithmetic against a declared registration date", the same
family this ADR's own reasoning discipline permits reusing when the
check OBJECT differs), but the comparison direction is inverted and the
regulatory concern is genuinely different. This is also different from
Dominica's dual authority-escalation ladder (object: the procuring
entity's OWN internal governance hierarchy for the award, not a bidder
date at all), Grenada's backward-looking director-conviction lookback
(object: a conviction date's disqualifying window, not a registration
date), and Estonia's closed-set signing-method validity (object: the
identity of a filing's own execution instrument, not a date at all).

**GitHub-code-search due diligence** (this fleet has 67 `iso3166-*`
repos in the `cloud-itonami` org -- too many to clone and read in full):
`gh api "search/code?q=org:cloud-itonami+filename:governor.cljc+..."`
returned zero hits for `premature`, `"lead time"`, and
`registration-lapsed` across the org's `governor.cljc` files at the time
this check was written, corroborating (without claiming to be an
exhaustive clone-all audit, which no single prior sibling's own ADR
claims either) that this MECHANIC/OBJECT pairing is not already in use
under an obvious name.

### Local Content Act 2021: genuinely investigated (per this task's own suggestion), TWO candidate flagship mechanics found, BOTH set aside

Given Guyana's ExxonMobil-led offshore oil-and-gas boom, this iteration
specifically investigated the Local Content Act 2021 (Act No. 18 of
2021, assented 29 December 2021, gazetted 31 December 2021 -- found via
`parliament.gov.gy`'s own paginated "Acts of Parliament" archive, whose
metadata table confirms the act number and both dates; downloaded via
that page's own signed download link, an entirely scanned-image-only
28-page PDF, `pdftoppm -r 300` + `tesseract` OCR'd directly) as a
flagship candidate, per this task's own suggestion that it "could make a
strong, genuinely novel flagship governor check if independently
confirmed."

Two candidate mechanics were found, genuinely real, and BOTH set aside:

- **s.2's "Guyanese company" definition**: a company incorporated under
  the Companies Act that is (a) beneficially owned by Guyanese nationals
  holding, individually or jointly, at least 51% of total issued shares,
  AND (b) has Guyanese nationals holding at least 75% of executive/
  senior-management positions AND at least 90% of non-managerial and
  other positions. This is a genuine, real, multi-criterion composite
  eligibility test (ownership percentage + two separate staffing
  percentages) -- but this fleet's OWN `cloud-itonami-iso3166-est`
  sibling's `governor.cljc` docstring, read directly while studying the
  family's conventions before writing this repo, lists "multi-criterion
  workforce-composition eligibility" among the check MECHANICS already
  in use elsewhere in this family. Building GUY's flagship on the same
  general mechanic risks being a re-skin, not a genuinely novel check
  shape, even though the specific numbers (51/75/90%) and the specific
  Act differ.
- **s.6(3)'s Local Content Register certificate**: "an annual
  certificate which shall become renewable on the anniversary date of
  the issuance of the certificate" -- a 1-year MAXIMUM validity window.
  This is mechanically the SAME shape as BRB's already-used 3-year
  Suppliers Register validity window (a maximum-validity-window expiry
  recompute) -- merely a different number of years, not a genuinely
  different check shape.

Rather than force either candidate into this repo's flagship slot, the
Local Content Act is instead cited HONESTLY in `src/statute/facts.cljc`
(this repo's second, orthogonal general-compliance catalog, exactly the
purpose that namespace exists for) as a genuinely real, currently
topical statute a petroleum-sector operator must track for compliance --
without a `marketentry.facts`/`marketentry.governor` check built on it.
README's "What this is NOT" section states this scoping explicitly. A
smaller, 100% honest flagship (the Register of Bidders lead-time check,
doubly corroborated from the Procurement Act family itself) beats a
broader one built on a re-skinned mechanic -- the same discipline this
task's own instructions and this family's prior ADRs (e.g. DMA's own
"candidates considered and set aside" section) apply.

### Other HARD checks (all unoverridable)

1. **spec-basis** -- never invent a jurisdiction's market-entry
   requirements (`marketentry.facts` G2 catalog: NPTA/National Board /
   Registrar of Companies / GRA for GUY).
2. **evidence-incomplete** -- draft/submit require a full assessment
   checklist on file.
3. **registration-lead-time-insufficient** -- see above (FLAGSHIP).
4. **engagement-fee-mismatch** -- recompute `base-fee + monthly-rate ×
   monitoring-months` (ground-truth-recompute discipline).
5. **tin-unverified** -- conditional on `:requires-tin?`. Grounded in
   the Guyana Revenue Authority's own published TIN application
   process, confirmed as a SEPARATE act from Registrar-of-Companies
   business registration (two-act model, like ATG/GRD/BRB) -- mirrors
   this family's shared TIN-check shape rather than inventing a new
   one, since only the FLAGSHIP check needs to be genuinely novel.
6. **already-drafted / already-submitted** -- dedicated booleans, never
   a `:status` value.

### `rep-spec-basis`: genuinely FOUND, an "affiliate" extension (not a "directors" extension)

The Procurement (Suspension and Debarment) Regulations 2019
(Regulations No. 5 of 2019, made under Procurement Act s.61 with the
advice of the Public Procurement Commission -- downloaded from
`npta.gov.gy`, entirely scanned-image-only, OCR'd directly) reg.14
"Scope of debarment", OCR'd verbatim: "The Commission may extend the
debarment or suspension order to any affiliate of the supplier or
contractor provided that any affiliate to which the order is to be
extended is given advance written notice ...". reg.2(1) defines
"affiliate" as "any business, organisation or any person that directly
or indirectly (a) controls or has the power to control the other, or
(b) [is] a third party in control or who has the power to control
both." This is a genuine, verified finding -- like DMA's s.80(6) and
BRB's s.88(2) -- but this ADR records the honest nuance: it is an
"affiliate" (control-relationship) extension, a broader and differently
defined concept than DMA's "directors" extension, and this catalog does
not flatten the two into identical wording.

### The one-act-vs-two-acts business-registration/tax question -- Guyana is a TWO-act model

The Companies Act (Cap. 89:01, Act No. 29 of 1991 -- downloaded directly
from `mola.gov.gy/laws-of-guyana` after paging through its chapter
listing to Chapter 089:01, a genuine text-layer PDF read via `pdftotext
-layout`) s.4 (incorporation) and s.8 (certificate of incorporation) are
entirely silent on tax registration. The Guyana Revenue Authority's own
"How To Obtain A TIN?" guidance page (fetched directly) lists, for a
Trade/Business applicant, "If Trade/Business is registered with
Commercial Registry: Business Registration Certificate" as a required
SUPPORTING document for the SEPARATE TIN application -- confirming
incorporation/business registration happens FIRST and TIN issuance is a
distinct, subsequent act. This is the same two-act shape ATG/GRD/BRB
document, NOT DMA's automatic same-transaction model.

**Honest gap, not a guess**: this iteration could NOT independently
confirm which Ministry the Companies Act's own s.2 Interpretation
assigns "Minister" to for s.468's "general supervision" of the
Registrar of Companies -- the term is not defined in s.2(1) itself
(unlike "Registrar", which IS: "the Registrar of Companies under this
Act"). The Ministry of Legal Affairs's apparent registrar subdomain
(`dcra.gov.gy`, which both `curl` and `WebFetch` confirmed 301-redirects
to `dcraguyana.org`) returned a Cloudflare interactive JavaScript-and-
cookie bot challenge on every fetch attempt of both the original and
redirect-target URL. This iteration did NOT attempt to defeat that
challenge (bot-detection bypass is out of scope for this work, per this
workspace's own safety floor). The exact parent Ministry is therefore
left as an honest, disclosed gap in `marketentry.facts`, not a guess.

### `statute.facts` (second, orthogonal catalog) -- four Guyana statutes, all independently confirmed

Companies Act (Cap. 89:01) + two separate labour statutes (the same
"wages/hours" + "termination/severance" two-statute split this family's
DMA sibling documents, rather than one consolidated labour code): the
Labour Act (Cap. 98:01, Act 2 of 1942, extensively amended through 1997)
and the Termination of Employment and Severance Pay Act (Cap. 96:01,
Act 19 of 1997, amended 1999) -- both downloaded directly from
`mola.gov.gy`, genuine text-layer PDFs, read via `pdftotext -layout` --
plus the Local Content Act 2021 (see above), all four independently
confirmed via primary-source text this iteration fetched and read
itself.

## Consequences

- `src/` now genuinely exists with real, tested, curl/pdftotext/OCR-
  cited content for this blueprint's declared domain (`:public-sector/
  market-entry-compliance`) -- moves this repo's
  `manifest/itonami-fleet-audit.edn` `:prod-ready?` signal from `:stub`
  to `:active`.
- The existing `culture.facts` catalog (Wave 1, unrelated batch) is
  untouched.
- The Local Content Act 2021's own composite ownership/staffing
  eligibility test and its 1-year Local Content Register certificate
  validity window are both genuine, verified, NOT-implemented extension
  points for a future iteration that wants a SECOND governor/domain
  (a distinct petroleum-sector local-content compliance actor, out of
  this repo's current market-entry scope) rather than a reason to force
  either into this repo's flagship slot.
- The parent Ministry of the Registrar of Companies remains an honest,
  disclosed gap (Cloudflare-blocked `dcra.gov.gy`/`dcraguyana.org`) for
  a future iteration to resolve without guessing.
- Sibling country blueprints can continue forking this family and
  swapping in their own genuinely-researched `marketentry.facts` /
  `statute.facts` content and whichever flagship check their own law
  actually supports -- this ADR is itself further evidence that the
  flagship check should be chosen from real, currency-checked research,
  not copied by rote or forced onto the most topical-sounding statute,
  and that a shared general MECHANIC (date arithmetic against a
  registration date) can still ground a genuinely different check
  OBJECT when the comparison direction and regulatory concern are
  inverted.
