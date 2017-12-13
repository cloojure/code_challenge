(ns toehold.opt-test
  (:use toehold.opt tupelo.test)
  (:require
    [clojure.math.combinatorics :as combo]
    [tupelo.array :as tar]
    [tupelo.core :as t]
    [tupelo.misc :as tm]
    [tupelo.schema :as tsk]
    [schema.core :as s]))
(t/refer-tupelo)

(dotest
  (is= empty-board [[:_ :_ :_]
                    [:_ :_ :_]
                    [:_ :_ :_]])
)

