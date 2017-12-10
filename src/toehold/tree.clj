(ns toehold.tree
  (:require [clojure.zip :as z]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]
            [clojure.math.combinatorics :as combo]
            [toehold.core :as c :refer :all]
            [tupelo.core :as t]
            [clojure.set :as set]))
(t/refer-tupelo)

(defrecord node [content children])

; Contentful-node zipper largely borrowed from
; http://grokbase.com/p/gg/clojure/12ag6cjnch/how-to-represent-trees-for-use-with-zippers
(defn content-zipper
  "vector-zip and seq-zip assume that branch nodes don't have content.
  This version is like vector-zip, but assumes all nodes can have content."
  [root]
  (z/zipper (comp coll? :children)
            :children
            (fn [nd children]
              (assoc nd :children children))
            root))

(defn content [loc] (:content (first loc)))

(defn node-str
  "Return the attractively formatted contents of a node"
  [loc]
  (when-let [node (z/node loc)]
    (str "> " (:content node))))

(defn print-tree [loc]
  (when-not (z/end? loc)
    (do (when (z/branch? loc)
          (pprint (str (string/join "" (repeat (count (z/path loc)) " "))
                       (node-str loc))))
        (recur (z/next loc)))))

(defn node-path "Return the simple path of nodes up to and including this location, including the location"
  [loc]
  (conj (z/path loc) (z/node loc)))

(def player-turns-cycle (cycle [:x :o]))

; CHALLENGE 2: Write a function to build a tree of all possible games. Explain
; why or why not it uses content-zipper (above).
;-----------------------------------------------------------------------------
; Solution #1: We don't use trees, but just find all possible permutations of moves.
;              This is much simpler than building trees.

(defn append-player-turn
  "Given a seq of moves like [ [0 0] [1 1] ...] appends a player to each move
  like [ [0 0 :x] [1 1 :o] ...] "
  [move-coords]
  (map #(t/append %1 %2) move-coords player-turns-cycle))

(defonce all-square-perms
  ; "A list of all permutations of the square coords => 9! (362,880)"
  (combo/permutations all-square-coords))

(defonce all-game-perms
  ; "A list of all game move combinations with :x going first. Includes all 9 moves for each game."
  (map append-player-turn all-square-perms))

(defonce all-game-perms-winners
  (map winning-player all-game-perms))

(defonce total-game-perms (count all-game-perms))
(defonce num-x-wins (count (filter #{:x} all-game-perms-winners)))
(defonce num-o-wins (count (filter #{:o} all-game-perms-winners)))
(defonce num-cats   (count (remove #{:o :x} all-game-perms-winners)))

; Solution #2
; You can build up trees of solutions using a function like this, but I'm not sure how it makes
; things better.  See unit test of all trees from [1 2 3]
(defn trees-from
  "builds all possible trees from the set of elements supplied (assumed unique)"
  [elems]
  (let [elems (into (sorted-set) elems)]
    (t/forv [root elems]
      (let [others (set/difference elems #{root})]
        (t/prepend root (trees-from others))) )))



; CHALLENGE 3: Is it possible to rewrite build-tree so that it's significantly
; more efficient in time and/or space? If so, what strategies do you see for
; that? Implement one.

; Statistics for all possible game permutations (X moves first)
;   total-game-perms   =    362880 (100.0 %)
;   num-x-wins         =    212256 ( 58.5 %)
;   num-o-wins         =    104544 ( 28.8 %)
;   num-cats           =     46080 ( 12.7 %)


; CHALLENGE 4: write code to answer some of the following questions:
; 1. What percentage of 100000 random games have no win?
;   ANSWER:  12.7%

; 2. Given a partial game in a particular state, which player if any has
;   a guaranteed win if they play optimally?

; 3. Under what conditions is player 2 (O) guaranteed a win?
;   ANSWER: Player 2 wins 28.8% of all random games.  Unless we go into a lot of detail, I'd
;           have to answer "When X plays badly enough".

; 4. Can X get a win if they blow 1st move?
;   ANSWER: Sure, if Y "plays badly enough". Although, this begs the question of what
;           it means to "blow the 1st move".
