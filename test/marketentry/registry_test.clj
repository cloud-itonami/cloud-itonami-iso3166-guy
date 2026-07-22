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

(deftest days-from-civil-matches-known-day-counts
  (testing "Howard Hinnant's days_from_civil, cross-checked against Python's datetime while writing this namespace"
    (is (= 0     (registry/days-from-civil 1970 1 1)))
    (is (= 18993 (registry/days-from-civil 2022 1 1)))
    (is (= 11016 (registry/days-from-civil 2000 2 29)))   ; leap day
    (is (= 19782 (registry/days-from-civil 2024 2 29)))   ; leap day
    (is (= 18059 (registry/days-from-civil 2019 6 12)))
    (is (= 18992 (registry/days-from-civil 2021 12 31)))
    (is (= 20655 (registry/days-from-civil 2026 7 21)))))

(deftest parse-iso-date-round-trips-days-from-civil
  (is (= (registry/days-from-civil 2026 7 21) (registry/parse-iso-date "2026-07-21")))
  (is (= (registry/days-from-civil 2026 6 1)  (registry/parse-iso-date "2026-06-01")))
  (is (nil? (registry/parse-iso-date nil)))
  (is (nil? (registry/parse-iso-date ""))))

(deftest earliest-eligible-bid-day-is-registration-plus-seven-days
  (is (= (registry/parse-iso-date "2026-06-08") (registry/earliest-eligible-bid-day "2026-06-01")))
  ;; month boundary: 2026-07-28 + 7 days = 2026-08-04
  (is (= (registry/parse-iso-date "2026-08-04") (registry/earliest-eligible-bid-day "2026-07-28")))
  (is (nil? (registry/earliest-eligible-bid-day nil))))

(deftest bidder-registration-lead-time-insufficient-recompute
  (testing "seven clear days between registration and submission -> sufficient (not insufficient)"
    (is (false? (registry/bidder-registration-lead-time-insufficient?
                 {:bidder-registration-date "2026-06-01" :submission-date "2026-07-21"}))))
  (testing "only three days between registration and submission -> insufficient"
    (is (true? (registry/bidder-registration-lead-time-insufficient?
                {:bidder-registration-date "2026-07-18" :submission-date "2026-07-21"}))))
  (testing "EXACTLY seven days -> sufficient (not STRICTLY before the earliest eligible day)"
    (is (false? (registry/bidder-registration-lead-time-insufficient?
                 {:bidder-registration-date "2026-07-14" :submission-date "2026-07-21"}))))
  (testing "six days (one short) -> insufficient"
    (is (true? (registry/bidder-registration-lead-time-insufficient?
                {:bidder-registration-date "2026-07-15" :submission-date "2026-07-21"}))))
  (testing "missing either date -> never treated as insufficient here (evidence-incomplete's job)"
    (is (false? (registry/bidder-registration-lead-time-insufficient? {:submission-date "2026-07-21"})))
    (is (false? (registry/bidder-registration-lead-time-insufficient? {:bidder-registration-date "2026-06-01"})))
    (is (false? (registry/bidder-registration-lead-time-insufficient? {})))))
