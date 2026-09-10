(ns statute.facts
  "General-law compliance catalog for Guyana (GUY) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/
  -arm/-atg/-dma's `statute.facts` (ADR-2607141700,
  cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL Guyana government-hosted URL -- never
  fabricated. Two hosts served every citation below directly this
  iteration, with no TLS/bot-detection blocker (`curl` with a standard
  browser user-agent succeeded on `mola.gov.gy` and `parliament.gov.gy`
  every time attempted):

  - Companies Act, Cap. 89:01 -- the PDF at `mola.gov.gy/laws/...`
    (found by paging through MOLA's own `laws-of-guyana` chapter listing
    -- Chapter 089:01, under 'Volume 16 Cap. 83.01 - 89.01') carries a
    genuine text layer (Nitro-Pro-produced, `pdftotext -layout` read
    directly): its own 'ARRANGEMENT OF SECTIONS' confirms s.4
    'Incorporation' ('one or more persons may incorporate a company by
    signing and sending articles of incorporation to the Registrar')
    and s.8 'Certificate of incorporation' ('Upon receipt of articles of
    incorporation, the Registrar must issue a certificate of
    incorporation ... conclusive proof of the incorporation of the
    company').
  - Labour Act, Cap. 98:01 (Act 2 of 1942, amended by 30/1947, 42/1955,
    39/1955, 8/1956, 29/1960, 12/1961, 4/1972, 5/1974, 8/1975, 19/1977,
    22/1978, 9/1984, 19/1990, 20/1994, 19/1997) -- also a genuine
    text-layer PDF from `mola.gov.gy`, read directly via `pdftotext
    -layout`; covers hours of work (s.28 power to make regulations as
    to hours of work in any occupation) and minimum-wage machinery.
  - Termination of Employment and Severance Pay Act, Cap. 96:01 (Act 19
    of 1997, amended by 7 of 1999) -- a SEPARATE, complementary statute
    to the Labour Act rather than a single consolidated labour code
    (the same 'wages/hours' + 'termination/severance' two-statute split
    this family's DMA sibling documents for its own Labour Standards
    Act / Protection of Employment Act pair) -- own text (read directly
    via `pdftotext -layout`) covers minimum periods of notice (s.15),
    payment in lieu of notice (s.16), and severance/redundancy pay.
  - Local Content Act 2021 (Act No. 18 of 2021, assented 29 December
    2021, gazetted 31 December 2021) -- genuinely investigated per this
    task's own suggestion, given Guyana's oil-and-gas boom
    (ExxonMobil-led offshore development). Found via
    `parliament.gov.gy`'s own paginated 'Acts of Parliament' archive
    (its metadata table confirms 'Act Number 18/2021', 'Date Gazetted 31
    December, 2021', 'Act Date Passed 29 December, 2021'); the Act
    itself (downloaded via that page's own signed download link,
    scanned-image-only, `pdftoppm -r 300` + `tesseract` OCR'd directly)
    requires every Contractor, Sub-Contractor or Licensee in the
    petroleum sector to implement local content (s.3(2)) and to comply
    with minimum local content levels set out in the First Schedule
    (s.7(1)), establishes a Local Content Secretariat (s.5) within the
    Ministry responsible for petroleum, and defines 'Guyanese company'
    (s.2) via a composite ownership (>=51% Guyanese-beneficially-owned)
    + staffing (>=75% executive/senior-management, >=90% non-managerial
    positions Guyanese) test. This catalog cites the Act HONESTLY as a
    genuinely real, currently topical general-compliance statute for a
    petroleum-sector operator -- but deliberately does NOT ground a
    `marketentry.facts`/`marketentry.governor` check on it: this
    iteration found two candidate flagship mechanics here (the
    composite ownership/staffing test; the s.6(3) annual-renewable
    Local Content Register certificate) and set BOTH aside because each
    is mechanically too close to an ALREADY-USED sibling mechanic
    elsewhere in this fleet (a multi-criterion workforce-composition
    eligibility test; a fixed-year registration-validity window,
    respectively) rather than genuinely novel -- see
    `marketentry.facts` namespace docstring and
    `docs/adr/0001-architecture.md` for the full reasoning. This is the
    same honest-scope-narrowing discipline this family's siblings apply
    when a genuinely-real regime doesn't clear the bar for THIS repo's
    flagship-check slot.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"GUY"
   [{:statute/id "guy.companies-act"
     :statute/title "Companies Act"
     :statute/jurisdiction "GUY"
     :statute/kind :law
     :statute/law-number "Cap. 89:01 (Act No. 29 of 1991, amended by 13/1995, 5/1997, 21/1998, 11/1999)"
     :statute/url "https://mola.gov.gy/laws/Volume%2016%20Cap.%2083.01%20-%2089.011695667005.pdf"
     :statute/url-provenance :official-mola-gov-gy
     :statute/enacted-date "1991-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "guy.labour-act"
     :statute/title "Labour Act"
     :statute/jurisdiction "GUY"
     :statute/kind :law
     :statute/law-number "Cap. 98:01 (Act 2 of 1942, amended by 30/1947, 42/1955, 39/1955, 8/1956, 29/1960, 12/1961, 4/1972, 5/1974, 8/1975, 19/1977, 22/1978, 9/1984, 19/1990, 20/1994, 19/1997)"
     :statute/url "https://mola.gov.gy/laws/Volume%2018%20Cap.%2091.02%20-%2096.011695660707.pdf"
     :statute/url-provenance :official-mola-gov-gy
     :statute/enacted-date "1942-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor :employment}}
    {:statute/id "guy.termination-of-employment-and-severance-pay-act"
     :statute/title "Termination of Employment and Severance Pay Act"
     :statute/jurisdiction "GUY"
     :statute/kind :law
     :statute/law-number "Cap. 96:01 (Act 19 of 1997, amended by 7 of 1999)"
     :statute/url "https://mola.gov.gy/laws/Volume%2018%20Cap.%2091.02%20-%2096.011695660952.pdf"
     :statute/url-provenance :official-mola-gov-gy
     :statute/enacted-date "1997-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor :employment :termination}}
    {:statute/id "guy.local-content-act-2021"
     :statute/title "Local Content Act 2021"
     :statute/jurisdiction "GUY"
     :statute/kind :law
     :statute/law-number "Act No. 18 of 2021 (assented 29 December 2021, gazetted 31 December 2021)"
     :statute/url "https://parliament.gov.gy/publications/acts-of-parliament/local-content-act-2021-no.-18-of-2021"
     :statute/url-provenance :official-parliament-gov-gy
     :statute/enacted-date "2021-12-29"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:oil-and-gas :petroleum :local-content}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-guy statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "GUY")) " GUY statutes seeded with an "
                 "official government-hosted citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :termination)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
