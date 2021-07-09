(ns org.vuxu.base45-test
  (:use [clojure.test :only [deftest is are testing]])
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check :as tc]
            [org.vuxu.base45 :refer :all]))

(deftest encode-test
  (testing "encoding examples"
    (are [r e] (= (encode r) e)
      "AB" "BB8"
      "Hello!!" "%69 VD92EX0"
      "base-45" "UJCLQE7W581"
      "\0" "00"
      "\0\0" "000"
      "\0\0\0" "00000"
      "\0\0\0\0" "000000")))

(deftest decode-test
  (testing "decoding examples"
    (are [r e] (= (decode r) e)
      "QED8WEX0" "ietf!"
      "00" "\0"
      "000" "\0\0"
      "00000" "\0\0\0"
      "000000" "\0\0\0\0")))

(deftest decode-error-detection-test
  (testing "decoding examples"
    (are [s] (thrown? IllegalArgumentException (decode s))
      "1234"
      "FOO@BA"
      "FOOGGW")))

(defspec roundtrip-spec 10000
  (prop/for-all [s gen/string]
                (= (decode (encode s)) s)))
