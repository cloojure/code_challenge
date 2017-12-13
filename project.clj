(defproject toehold "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
     [org.clojure/clojure                 "1.9.0"]
     [org.clojure/core.logic              "0.8.11"]
     [pjstadig/humane-test-output         "0.8.3"]
     [prismatic/schema                    "1.1.7"]
     [tupelo                              "0.9.68"]
  ]
  :main ^:skip-aot toehold.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.14.0"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}})
