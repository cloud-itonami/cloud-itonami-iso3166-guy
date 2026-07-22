(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest guy-has-spec-basis
  (let [sb (facts/spec-basis "GUY")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "GUY")))
    (is (some? (facts/business-registration-spec-basis "GUY")))
    (is (some? (facts/registration-lead-time-spec-basis "GUY")))))

(deftest guy-rep-spec-basis-is-genuinely-present
  (testing "Procurement (Suspension and Debarment) Regulations 2019 reg.14 extends debarment/suspension to any affiliate of the supplier or contractor -- a genuine, verified finding, not honestly-nil"
    (let [rb (facts/rep-spec-basis "GUY")]
      (is (some? rb))
      (is (string? (:rep-owner-authority rb)))
      (is (string? (:rep-legal-basis rb))))))

(deftest guy-business-registration-is-a-separate-body-from-tax-and-procurement
  (testing "business registration (Registrar of Companies, Companies Act Cap. 89:01) is a DIFFERENT body than the tax registrar (GRA) and the procurement regulator (NPTA/National Board) -- see namespace docstring"
    (let [reg (facts/business-registration-spec-basis "GUY")
          tax (facts/corporate-number-spec-basis "GUY")]
      (is (some? reg))
      (is (some? tax))
      (is (not= (:business-registration-owner-authority reg)
                (:corporate-number-owner-authority tax))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ")))
  (is (nil? (facts/business-registration-spec-basis "ATL")))
  (is (nil? (facts/registration-lead-time-spec-basis "ATL"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "GUY")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "GUY" all)))
    (is (not (facts/required-evidence-satisfied? "GUY" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["GUY" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))
