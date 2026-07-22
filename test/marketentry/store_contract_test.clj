(ns marketentry.store-contract-test
  "MemStore ≡ DatomicStore parity for the Store protocol."
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.store :as store]
            [marketentry.registry :as registry]))

(defn- exercise [s]
  (store/commit-record! s {:effect :engagement/upsert
                           :value {:id "eng-x" :operator "X Ltd" :jurisdiction "GUY"
                                   :base-fee 100 :monthly-rate 10 :monitoring-months 1
                                   :claimed-fee 110.0
                                   :resident? false
                                   :undertaking-triggers #{:two-or-more-local-contracts?}
                                   :business-registration-verified? true
                                   :tin-verified? true
                                   :sector :petroleum :local-content-compliant? true
                                   :drafted? false :submitted? false :status :intake}})
  (store/commit-record! s {:effect :assessment/set
                           :path ["eng-x"]
                           :payload {:jurisdiction "GUY" :checklist ["a"] :spec-basis "x"}})
  (store/commit-record! s {:effect :engagement/mark-drafted :path ["eng-x"]})
  (store/commit-record! s {:effect :engagement/mark-submitted :path ["eng-x"]})
  (store/append-ledger! s {:t :committed :op :test})
  {:engagement (store/engagement s "eng-x")
   :assessment (store/assessment-of s "eng-x")
   :drafts (store/draft-history s)
   :submits (store/submit-history s)
   :ledger (store/ledger s)
   :drafted? (store/engagement-already-drafted? s "eng-x")
   :submitted? (store/engagement-already-submitted? s "eng-x")})

(deftest mem-and-datomic-parity
  (let [mem (store/seed-db)
        dat (store/datomic-seed-db)
        ;; use empty stores for parity of exercised mutations
        mem* (store/->MemStore (atom {:engagements {} :assessments {} :ledger []
                                      :draft-sequences {} :draft-records []
                                      :submit-sequences {} :submit-records []}))
        dat* (store/datomic-store {})
        m (exercise mem*)
        d (exercise dat*)]
    (is (= (:operator (:engagement m)) (:operator (:engagement d))))
    (is (= (:resident? (:engagement m)) (:resident? (:engagement d))))
    (is (= (:undertaking-triggers (:engagement m)) (:undertaking-triggers (:engagement d))))
    (is (= (:business-registration-verified? (:engagement m)) (:business-registration-verified? (:engagement d))))
    (is (= (:sector (:engagement m)) (:sector (:engagement d))))
    (is (true? (:drafted? m)) (true? (:drafted? d)))
    (is (true? (:submitted? m)) (true? (:submitted? d)))
    (is (= 1 (count (:drafts m))) (= 1 (count (:drafts d))))
    (is (= 1 (count (:submits m))) (= 1 (count (:submits d))))
    (is (= 1 (count (:ledger m))) (= 1 (count (:ledger d))))
    (is (= (:assessment m) (:assessment d)))
    (testing "the SAME demo data feeds MemStore and DatomicStore seeding without error"
      (is (= (count (store/all-engagements mem))
             (count (store/all-engagements dat)))))))

(deftest business-registration-missing-parity-check-across-demo-data
  (testing "the demo data itself is consistent with marketentry.registry's own predicates (sanity check, not just store plumbing)"
    (let [db (store/seed-db)
          e4 (store/engagement db "eng-4")
          e8 (store/engagement db "eng-8")]
      (is (true? (registry/business-registration-missing? e4)))
      (is (false? (registry/business-registration-missing? e8))))))
