(ns org.vuxu.base45-test
  {:clj-kondo/config '{:lint-as {clojure.test.check.clojure-test/defspec
                                 clj-kondo.lint-as/def-catch-all}}}
  (:require [clojure.test :refer [deftest #_is are testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [org.vuxu.base45 :as base45]))

(deftest encode-test
  (testing "encoding examples"
    (are [r e] (= (base45/encode r) e)
      "AB" "BB8"
      "Hello!!" "%69 VD92EX0"
      "base-45" "UJCLQE7W581"
      "\0" "00"
      "\0\0" "000"
      "\0\0\0" "00000"
      "\0\0\0\0" "000000")))

(deftest decode-test
  (testing "decoding examples"
    (are [r e] (= (base45/decode r) e)
      "QED8WEX0" "ietf!"
      "00" "\0"
      "000" "\0\0"
      "00000" "\0\0\0"
      "000000" "\0\0\0\0")))

(deftest decode-error-detection-test
  (testing "decoding examples"
    (are [s] (thrown? IllegalArgumentException (base45/decode s))
      "1234"
      "FOO@BA"
      "FOOGGW")))

(defspec roundtrip-spec 10000
  (prop/for-all [s gen/string]
                (= (base45/decode (base45/encode s)) s)))
