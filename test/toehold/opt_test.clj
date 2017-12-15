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
  (when-not is-2x2-testing
    (is= empty-board [[:_ :_ :_]
                      [:_ :_ :_]
                      [:_ :_ :_]])

    (is= (open-moves empty-board)
      [[0 0] [0 1] [0 2]
       [1 0] [1 1] [1 2]
       [2 0] [2 1] [2 2]]))

  (is= :none (winner [[:_ :_ :_]
                      [:x :x :o]
                      [:x :x :o]]))

  (throws? (winner [[:x :_ :o]
                    [:x :_ :o]
                    [:x :_ :o]]))

  (is= :x (winner [[:_ :o :o]
                   [:x :_ :o]
                   [:x :x :x]]))
  (is (game-won? [[:_ :o :o]
                  [:x :_ :o]
                  [:x :x :x]]))

  (is= :x (next-turn empty-board))
  (is= :o (next-turn [[:_ :_ :o]
                      [:x :_ :o]
                      [:x :_ :x]]))

  (is= (make-move [[:_ :_ :o]
              [:x :_ :o]
              [:x :_ :x]] [0 0])
    [[:o :_ :o]
     [:x :_ :o]
     [:x :_ :x]] )
  (throws? (make-move [[:o :_ :o]
                  [:x :_ :o]
                  [:x :_ :x]] [0 0]))
  (throws? (make-move [[:x :_ :o]
                  [:x :_ :o]
                  [:x :_ :x]] [0 0]))

  (is= (open-moves [[:_ :_ :_]
                    [:x :x :o]
                    [:x :x :o]])
    [[0 0] [0 1] [0 2]])

  (is= (open-moves [[:x :_ :o]
                    [:x :_ :o]
                    [:x :_ :o]])
    [[0 1] [1 1] [2 1]])

  (is= (open-moves [[:_ :o :o]
                    [:x :_ :o]
                    [:x :x :_]])
    [[0 0] [1 1] [2 2]])

  (is= (open-moves [[:_ :o :o]
                    [:x :_ :o]
                    [:x :x :x]])
    [[0 0] [1 1]])
  )

(dotest
  ; Test with smaller board
  (when is-2x2-testing
    (is= empty-board [[:_ :_]
                      [:_ :_]])
    (is= (open-moves empty-board) ; #todo 2x2 testing
      [[0 0] [0 1]
       [1 0] [1 1]])
    (is= (open-moves [[:_ :o]
                      [:x :_]])
      [[0 0] [1 1]])

    (spyx-pretty (play-out empty-board))

  ))

