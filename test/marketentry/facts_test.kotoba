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
    (is (some? (facts/rep-spec-basis "GUY")))
    (is (some? (facts/local-content-spec-basis "GUY")))))

(deftest guy-owner-authority-is-npta-not-nptab
  (testing "the current official self-branding is NPTA (without 'Board') -- 'NPTAB' is deliberately not used"
    (let [sb (facts/spec-basis "GUY")]
      (is (re-find #"NPTA" (:owner-authority sb)))
      (is (not (re-find #"NPTAB" (:owner-authority sb)))))))

(deftest guy-local-content-is-sector-gated
  (testing "the Local Content Act 2021 spec-basis is present and marked petroleum-only"
    (let [lc (facts/local-content-spec-basis "GUY")]
      (is (some? lc))
      (is (= :petroleum (:local-content-sector lc))))))

(deftest guy-rep-spec-basis-is-public-procurement-commission
  (testing "procurement-oversight/debarment basis is the PPC, grounded in Article 212W"
    (let [rb (facts/rep-spec-basis "GUY")]
      (is (some? rb))
      (is (re-find #"Public Procurement Commission" (:rep-owner-authority rb)))
      (is (re-find #"212W" (:rep-legal-basis rb))))))

(deftest guy-business-registration-is-a-separate-body-from-tax-and-procurement
  (testing "business registration (DCRA) is a DIFFERENT body than the tax registrar (GRA) and the procurement regulator (NPTA)"
    (let [reg (facts/business-registration-spec-basis "GUY")
          tax (facts/corporate-number-spec-basis "GUY")
          sb (facts/spec-basis "GUY")]
      (is (some? reg))
      (is (some? tax))
      (is (not= (:business-registration-owner-authority reg)
                (:corporate-number-owner-authority tax)))
      (is (not= (:business-registration-owner-authority reg)
                (:owner-authority sb))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ")))
  (is (nil? (facts/business-registration-spec-basis "ATL")))
  (is (nil? (facts/local-content-spec-basis "ATL"))))

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
