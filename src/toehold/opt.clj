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
(def Player (s/enum :x :o :none))

(s/def empty-board :- Board
  (tar/create 3 3 :_))

(s/defn unused? :- s/Bool
  "Returns true if a board square is unused by :x or :o"
  [board :- Board
   ii :- s/Int
   jj :- s/Int]
  (= :_ (tar/elem-get board ii jj)))

(s/defn open-moves  :- [Move]
  "Returns a list of possible moves given a game-state board"
  [board :- tar/Array]
  (keep-if not-nil?
    (forv [ii (range 3)
           jj (range 3)]
      (when (unused? board ii jj)
        [ii jj]))) )

; winner Node
(def node-winner
  {:board  [[:_ :o :o]
            [:x :_ :o]
            [:x :x :x]]
   :winner :x
   :kids   {[0 0 :x] :node-next-0
            [1 1 :x] :node-next-1
            [2 2 :x] :node-next-2
            }})

; general Node example
(def node-general
  {:board  [[:_ :o :o]
            [:x :_ :o]
            [:x :x :_]]
   :winner :none
   :kids   {[0 0 :x] :node-next-0
            [1 1 :x] :node-next-1
            [2 2 :x] node-winner
            }})

(s/defn board-rows :- [tsk/Triple]
  "Returns the rows of the board"
  [board]
  (tar/rows-get board))
(s/defn board-cols :- [tsk/Triple]
  "Returns the cols of the board"
  [board]
  (tar/cols-get board))
(s/defn board-diags :- [tsk/Triple]
  "Returns the cols of the board"
  [board]
  [(forv [ii (range 3)] (tar/elem-get board ii ii))
   (forv [ii (range 3)] (tar/elem-get board ii (- 2 ii)))])

(s/defn triple-winner
  "If a triple has a winner like [:x :x :x] return it, else :none"
  [triple :- tsk/Triple]
  (cond
    (= [:x :x :x] triple) :x
    (= [:o :o :o] triple) :o
    :else :none))

(s/defn winner :- Player
  "Returns one of #{ :x :o :none } to indicate the game state. Throws if board has more than one winner. "
  [board]
  (let [all-triples (glue
                      (board-rows board)
                      (board-cols board)
                      (board-diags board))
        >> (spyx-pretty all-triples)
        winners     (drop-if #(= % :none)
                      (spyx (mapv triple-winner all-triples)))
        >> (spyx winners)
        num-winners (count winners)]
    (cond
      (< 1 num-winners) (throw (IllegalStateException. (str "winner: too many winners found! board=" board)))
      (= 1 num-winners) (only winners)
      (zero? num-winners) :none
      )

    )
  )

