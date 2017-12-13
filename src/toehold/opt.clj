(ns toehold.opt
  (:require
    [clojure.math.combinatorics :as combo]
    [tupelo.array :as tar]
    [tupelo.core :as t]
    [tupelo.misc :as tm]
    [tupelo.schema :as tsk]
    [schema.core :as s]))
(t/refer-tupelo)

(def Move tsk/Pair) ; a move like [1 2]
(def Board [(s/one tsk/Triple "row1")
            (s/one tsk/Triple "row2")
            (s/one tsk/Triple "row3")] )

(s/def empty-board :- Board
  (tar/create 3 3 :_))

(s/defn unused? :- s/Bool
  "Returns true if a board square is unused by :x or :o"
  [board :- Board
   ii :- s/Int
   jj :- s/Int]
  )

;(s/defn open-moves  :- [Move]
;  "Returns a list of possible moves given a game-state board"
;  [board :- tar/Array]
;  (keep-if not-nil?
;    (forv [ii (range 3)
;           jj (range 3)]
;      (when )))
;  )

