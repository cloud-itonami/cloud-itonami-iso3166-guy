(ns statute.facts-test
  (:require [kotoba.lang.text :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest guy-has-spec-basis
  (let [sb (facts/spec-basis "GUY")]
    (is (= 4 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["GUY" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= #{"guy.labour-act" "guy.termination-of-employment-and-severance-pay-act"}
         (set (mapv :statute/id (facts/by-topic "GUY" :labor)))))
  (is (= ["guy.termination-of-employment-and-severance-pay-act"]
         (mapv :statute/id (facts/by-topic "GUY" :termination))))
  (is (= ["guy.local-content-act-2021"]
         (mapv :statute/id (facts/by-topic "GUY" :petroleum))))
  (is (empty? (facts/by-topic "GUY" :environment)))
  (is (empty? (facts/by-topic "ATL" :labor))))
