(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "GUY" 0)
        s (registry/register-submit "eng-1" "GUY" 0)]
    (is (= "GUY-DFT-000000" (get d "draft_number")))
    (is (= "GUY-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "GUY" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

;; --------------- Companies Act 1991 non-resident registration gate ---------------

(deftest resident-entity-always-requires-registration
  (testing "a RESIDENT Guyanese entity must always be registered -- no trigger conditionality"
    (is (true? (registry/business-registration-missing?
                {:resident? true :business-registration-verified? false})))
    (is (false? (registry/business-registration-missing?
                 {:resident? true :business-registration-verified? true})))))

(deftest non-resident-entity-is-conditional-on-triggers
  (testing "a NON-RESIDENT entity that trips a Companies Act 1991 trigger must be registered"
    (is (true? (registry/non-resident-registration-required?
                {:resident? false :undertaking-triggers #{:two-or-more-local-contracts?}})))
    (is (true? (registry/business-registration-missing?
                {:resident? false :undertaking-triggers #{:two-or-more-local-contracts?}
                 :business-registration-verified? false}))))
  (testing "a NON-RESIDENT entity that trips NO trigger is NOT yet required to register"
    (is (false? (registry/non-resident-registration-required?
                 {:resident? false :undertaking-triggers #{}})))
    (is (false? (registry/business-registration-missing?
                 {:resident? false :undertaking-triggers #{}
                  :business-registration-verified? false})))
    (is (false? (registry/business-registration-missing?
                 {:resident? false :undertaking-triggers nil
                  :business-registration-verified? false}))))
  (testing "a NON-RESIDENT entity that trips a trigger AND is verified is fine"
    (is (false? (registry/business-registration-missing?
                 {:resident? false :undertaking-triggers #{:appointed-resident-agent?}
                  :business-registration-verified? true})))))

;; --------------- Local Content Act 2021 (sector-gated) ---------------

(deftest local-content-act-applies-only-to-petroleum-sector
  (testing "the Local Content Act 2021 applies ONLY to petroleum-sector engagements"
    (is (true? (registry/local-content-act-applies? {:sector :petroleum})))
    (is (false? (registry/local-content-act-applies? {:sector :general})))
    (is (false? (registry/local-content-act-applies? {:sector :construction})))
    (is (false? (registry/local-content-act-applies? {})))))

(deftest local-content-noncompliant-is-sector-conditional
  (testing "petroleum sector, not compliant -> noncompliant"
    (is (true? (registry/local-content-noncompliant?
                {:sector :petroleum :local-content-compliant? false}))))
  (testing "petroleum sector, compliant -> not noncompliant"
    (is (false? (registry/local-content-noncompliant?
                 {:sector :petroleum :local-content-compliant? true}))))
  (testing "NON-petroleum sector NEVER fires this check, even with the same 'noncompliant' flag"
    (is (false? (registry/local-content-noncompliant?
                 {:sector :general :local-content-compliant? false}))))
  (testing "missing sector NEVER fires this check"
    (is (false? (registry/local-content-noncompliant? {:local-content-compliant? false})))))
