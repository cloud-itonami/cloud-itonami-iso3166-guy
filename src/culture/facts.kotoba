(ns culture.facts
  "Country-level regional-culture catalog for Guyana (GUY) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"GUY"
   [{:culture/id "guy.dish.pepperpot"
     :culture/name "Guyanese pepperpot"
     :culture/country "GUY"
     :culture/kind :dish
     :culture/summary "Slow-cooked meat stew, one of Guyana's national dishes, traditionally served at Christmas and made with meat, cinnamon, hot peppers and cassareep."
     :culture/url "https://en.wikipedia.org/wiki/Guyanese_pepperpot"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "guy.dish.cook-up-rice"
     :culture/name "Cook-up rice"
     :culture/country "GUY"
     :culture/kind :dish
     :culture/summary "One-pot meal, the local variation on Anglo-Caribbean rice and peas, among the most frequently prepared dishes in Guyana."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Guyana"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "guy.dish.metemgee"
     :culture/name "Metemgee"
     :culture/country "GUY"
     :culture/kind :dish
     :culture/summary "Thick, rich Guyanese soup with ground provisions, coconut milk and large dumplings called duff, typically eaten with fried fish or chicken."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Guyana"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "guy.dish.curry"
     :culture/name "Guyanese curry"
     :culture/country "GUY"
     :culture/kind :dish
     :culture/summary "Curry is widely popular in Guyana, reflecting Indian influences, with variations including chicken, seafood, goat, lamb and duck."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Guyana"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "guy.product.demerara-sugar"
     :culture/name "Demerara sugar"
     :culture/country "GUY"
     :culture/kind :product
     :culture/summary "Demerara sugar is so named because it originally came from sugarcane fields in the colony of Demerara, a historical region in present-day Guyana."
     :culture/url "https://en.wikipedia.org/wiki/Demerara"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "guy.festival.mashramani"
     :culture/name "Mashramani"
     :culture/country "GUY"
     :culture/kind :festival
     :culture/summary "Annual festival held on 23 February, Guyana's Republic Day, celebrating the nation's transition to a republic in 1970 with parades, music competitions and costume contests."
     :culture/url "https://en.wikipedia.org/wiki/Mashramani"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-guy culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "GUY"))
                 " GUY entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
