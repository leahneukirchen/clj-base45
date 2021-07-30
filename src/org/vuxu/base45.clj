; https://datatracker.ietf.org/doc/draft-faltstrom-base45/
(ns org.vuxu.base45)

(def ^:const alphabet
  "The Base45 Alphabet"
  (vec (concat
        (map char (range (int \0) (inc (int \9))))
         (map char (range (int \A) (inc (int \Z))))
         '(\space \$ \% \* \+ \- \. \/ \:))))

(def ^:const reverse-alphabet
  "Reverse lookup for the Base45 Alphabet"
  (->> alphabet
       (map-indexed #(vector %2 %1))
       (into {})))

(defn encode [string]
  (transduce (comp
              (map (fn [b]                   ; make unsigned bytes
                     (cond-> b
                       (neg? b) (+ 256))))
              (partition-all 2)
              (mapcat (fn [[a b]]
                        (if b
                          (let [n (+ (* 256 a) b)
                                e (quot n (* 45 45))
                                n (mod n (* 45 45))
                                d (quot n 45)
                                c (mod n 45)]
                            [c d e])
                          [(mod a 45) (quot a 45)])))
              (map alphabet))
             str
             (.getBytes string "UTF8")))

(defn decode [string]
  (when (-> string count (mod 3) (= 1))
    (throw (IllegalArgumentException. "invalid length, is 1 mod 3")))
  (->> string
       (into []
             (comp
              (map reverse-alphabet)
              (map #(or %1
                        (throw (IllegalArgumentException. "invalid character"))))
              (partition-all 3)
              (mapcat (fn [[a b c]]
                        (if c
                          (let [n (+ a (* 45 b) (* 45 45 c))]
                            (when (> n 65535)
                              (throw (IllegalArgumentException.
                                      (str "triplet too high: " n))))
                            ((juxt quot mod) n 256))
                          [(+ a (* 45 b))])))))
       byte-array
       (#(String. % "UTF8"))))
