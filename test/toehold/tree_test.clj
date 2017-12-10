(ns toehold.tree-test
  (:require
    [clojure.test :refer :all]
    [clojure.math.combinatorics :as combo]
    [clojure.zip :as z]
    [toehold.core :as c :refer :all]
    [toehold.tree :as ttree :refer :all]
    [tupelo.core :as t]
    [tupelo.misc :as tm]
    [tupelo.test :as tt]
    [clojure.set :as set])
  (:import [toehold.tree node]))
; (t/refer-tupelo)    ; #todo remove

(defn v->nodes "Build a simple tree of nodes from a nested vector"
  [vectr]
  (node. (first vectr) (mapv v->nodes (rest vectr))))

(def x1 [17 [3] [14 [23] [11 [8]]]])
(def nodes (v->nodes x1))
(def z1 (content-zipper nodes))

(print-tree z1)
(comment
  "> 17"
  " > 3"
  " > 14"
  "  > 23"
  "  > 11"
  "   > 8"
  )

;; Handy zipper shortcuts for quicker testing:
(def z-l z/left)
(def z-r z/right)
(def z-d z/down)
(def con content)

(deftest basic-zipper-navigation-test
  (are [x y] [= x y]
    3 (-> z1 z-d con)
    8 (-> z1 z-d z-r z-d z-r z-d con)))

(def content-map #(map :content %))

(deftest node-path-test
  (are [x y] [= x y]
    '(17 14 11 8) (-> z1 z-d z-r z-d z-r z-d node-path content-map)))

(defn percent [numer denom] (* 100.0 (/ numer denom)))

(tt/dotest
  (tt/is= [:x :o :x :o :x] (take 5 player-turns-cycle))
  (tt/is= (append-player-turn all-square-coords)
    [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [1 2 :o] [2 0 :x] [2 1 :o] [2 2 :x]])

  (tt/is= (combo/permutations [1 2 3])
    [[1 2 3] [1 3 2] [2 1 3] [2 3 1] [3 1 2] [3 2 1]])

  (tt/is= (count all-square-perms) (tm/factorial 9))
  (tt/is= (take 9 all-square-perms)
    [[[0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 1] [2 2]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 2] [2 1]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 1] [2 0] [2 2]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 1] [2 2] [2 0]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 2] [2 0] [2 1]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 2] [2 1] [2 0]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [2 0] [1 2] [2 1] [2 2]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [2 0] [1 2] [2 2] [2 1]]
     [[0 0] [0 1] [0 2] [1 0] [1 1] [2 0] [2 1] [1 2] [2 2]]])

  (tt/is= (take 9 all-game-perms)
    [[[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [1 2 :o] [2 0 :x] [2 1 :o] [2 2 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [1 2 :o] [2 0 :x] [2 2 :o] [2 1 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [1 2 :o] [2 1 :x] [2 0 :o] [2 2 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [1 2 :o] [2 1 :x] [2 2 :o] [2 0 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [1 2 :o] [2 2 :x] [2 0 :o] [2 1 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [1 2 :o] [2 2 :x] [2 1 :o] [2 0 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [2 0 :o] [1 2 :x] [2 1 :o] [2 2 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [2 0 :o] [1 2 :x] [2 2 :o] [2 1 :x]]
     [[0 0 :x] [0 1 :o] [0 2 :x] [1 0 :o] [1 1 :x] [2 0 :o] [2 1 :x] [1 2 :o] [2 2 :x]]] )

  (tt/is= (reductions conj [] [1 2 3 4 5])
    [ [] [1] [1 2] [1 2 3] [1 2 3 4] [1 2 3 4 5] ])

  (tt/is= :x (winning-player [[0 0 :x] [0 1 :x] [0 2 :x]]))
  (tt/is= :x (winning-player [[0 0 :x] [0 1 :x] [0 2 :x]
                              [1 0 :o] [1 1 :o] [1 2 :o]]))
  (tt/is= :o (winning-player [[0 0 :x]
                              [1 0 :o] [1 1 :o] [1 2 :o]
                              [0 1 :x] [0 2 :x]]))
  (tt/is= #{nil :o :x} (set all-game-perms-winners))

  (tt/is= total-game-perms (+ num-x-wins num-o-wins num-cats))
  (println (format "total-game-perms   = %9d (%5.1f %%)" total-game-perms 100.0))
  (println (format "num-x-wins         = %9d (%5.1f %%)" num-x-wins
             (percent num-x-wins total-game-perms)))
  (println (format "num-o-wins         = %9d (%5.1f %%)" num-o-wins
             (percent num-o-wins total-game-perms)))
  (println (format "num-cats           = %9d (%5.1f %%)" num-cats
             (percent num-cats total-game-perms)))
)

(tt/dotest
  ; Hiccup-style formatting of trees from #{1 2 3}
  (tt/is= (trees-from #{1 2 3})
    [[1
      [2
       [3]]
      [3
       [2]]]
     [2
      [1
       [3]]
      [3
       [1]]]
     [3
      [1
       [2]]
      [2
       [1]]]])

  )


