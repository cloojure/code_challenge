(ns toehold.opt
  (:require
    [clojure.math.combinatorics :as combo]
    [tupelo.array :as tar]
    [tupelo.core :as t]
    [tupelo.misc :as tm]
    [tupelo.schema :as tsk]
    [schema.core :as s]))
(t/refer-tupelo)

(def is-2x2-testing true)

(def Player (s/enum :x :o :none))

(def Move tsk/Pair) ; a move like [1 2]

(when-not is-2x2-testing
  (def init-board-size 3)
  (def Board [(s/one tsk/Triple "row1")
              (s/one tsk/Triple "row2")
              (s/one tsk/Triple "row3") ])
  (def initial-moves-x
    "Restrict initial moves by X to only the non-symmetric ones."
    [ [0 0] [0 1] [1 1] ])
)

(when is-2x2-testing
  (def Board tsk/TupleList)
  (def init-board-size 2)
  (def initial-moves-x
    "Restrict initial moves by X to only the non-symmetric ones."
    [ [0 0] ])
)

(s/def empty-board :- Board
  (tar/create init-board-size init-board-size :_))

; assumes the board is always square
(defn board-size [board] (tar/num-rows board))
(defn board-idxs [board] (range (board-size board)))

(s/defn unused? :- s/Bool
  "Returns true if a board square is unused by :x or :o"
  [board :- Board
   ii :- s/Int
   jj :- s/Int]
  (= :_ (tar/elem-get board ii jj)))

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

(s/defn board-rows :- [tsk/Tuple]
  "Returns the rows of the board"
  [board]
  (tar/rows-get board))

(s/defn board-cols :- [tsk/Tuple]
  "Returns the cols of the board"
  [board]
  (tar/cols-get board))

(s/defn board-diags :- [tsk/Tuple]
  "Returns the diagonals of the board"
  [board]
  (vector
    ; main- diag
    (forv [ii (board-idxs board)]
      (tar/elem-get board ii ii))
    ; anti- diag
    (t/map-let [ii (board-idxs board)
                jj (reverse (board-idxs board))]
      (tar/elem-get board ii jj))))

(s/defn line-winner :- Player
  "If a line has a winner like [:x :x :x] return the winner, else :none"
  [line :- tsk/Tuple]
  (cond
    (apply = :x line) :x
    (apply = :o line) :o
    :else :none))

(s/defn winner :- Player
  "Returns one of #{ :x :o :none } to indicate the game state. Throws if board has more than one winner. "
  [board :- Board]
  (let [all-lines (glue
                      (board-rows board)
                      (board-cols board)
                      (board-diags board))
        winners     (drop-if #(= % :none)
                      (mapv line-winner all-lines))
        num-winners (count winners)]
    (cond
      (< 1 num-winners) (throw (IllegalStateException. (str "winner: too many winners found! board=" board)))
      (= 1 num-winners) (only winners)
      (zero? num-winners) :none)))

(s/defn game-won? :- s/Bool
  "Returns one of #{ :x :o :none } to indicate the game state. Throws if board has more than one winner. "
  [board :- Board]
  (not= :none (winner board)))

(s/defn next-turn
  "Returns :x or :o to indicate the next player with whos turn it is"
  [board :- Board]
  (let [all-squares (tar/array->row-data board)
        num-x       (count (keep-if #{:x} all-squares))
        num-o       (count (keep-if #{:o} all-squares))]
    (cond
      (= num-x num-o) :x
      (= num-x (inc num-o)) :o
      :else (throw (IllegalStateException. (str "next-turn: illegal board found! board=" board))))))

(s/defn make-move :- Board
  "Record a move to the indicated square, by the player with the next turn."
  [board :- Board
   move :- tsk/Pair]
  (let [[irow icol] move
        mover (next-turn board)]
    (when-not (unused? board irow icol)
      (throw (IllegalStateException. (str "move: space occupied! board=" board "   move=" move))))
    (tar/elem-set board irow icol mover)))

(s/defn open-moves  :- [Move]
  "Returns a list of possible moves given a game-state board"
  [board :- tar/Array]
  (keep-if not-nil?
    (let [idxs (board-idxs board)]
      (forv [ii idxs
             jj idxs]
        (when (unused? board ii jj)
          [ii jj])))) )

(defn play-out
  "play all possible games given the input board"
  [board]
  (let [possible-moves (if (= board empty-board)
                         initial-moves-x
                         (open-moves board))
        result-tree    (apply glue
                         (forv [move possible-moves]
                           (let [board-new (make-move board move) ]
                             (if (game-won? board-new)
                               {move {:board  board-new
                                      :winner (winner board-new)
                                      :kids   []}}
                               {move {:board board-new
                                      :winner nil
                                      :kids  (play-out board-new)}}
                             ))))
       ]
    result-tree
  ) )

