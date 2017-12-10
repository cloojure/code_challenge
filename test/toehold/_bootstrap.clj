(ns toehold._bootstrap
  "This namespace is used to perform one-time tasks during testing, such as printing the
  Clojure version."
  (:use tupelo.test )
  (:require [schema.core :as s]
            [tupelo.core :as t] ))
(t/refer-tupelo)

; Prismatic Schema type definitions
; #todo add to Schema docs
(s/set-fn-validation! true) ; enforce fn schemas

; (set! *warn-on-reflection* true)  ; #todo enable?

(dotest
  (t/print-versions)
  ;(spyx (s/fn-validation?))
  )
