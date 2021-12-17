(ns emilaasa.clj-ds-demo
  (:require [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            ))

;; A this is how we start clerk. Having these commands outside a comment block will cause an infinite loop.
(comment
  (clerk/serve! {:browse? true
                 :watch-paths ["src"]})
  (clerk/show! "src/emilaasa/clj_ds_demo.clj"))

;; # Markdown
;; Full __support__ for [markdown](https://codescene.io)
;; |  this |  be |   |   |   |
;; |---|---|---|---|---|
;; |  could |  cool |   |   |   |
(clerk/plotly {:data [{:z [[1 2 3] [3 2 1]] :type "surface"}]})
(clerk/vl {:width 650
           :height 400
           :data {:url "https://vega.github.io/vega-datasets/data/us-10m.json"
                  :format {:type "topojson" :feature "counties"}}
           :transform [{:lookup "id"
                        :from {:data {:url "https://vega.github.io/vega-datasets/data/unemployment.tsv"}
                               :key "id"
                               :fields ["rate"]}}]
           :projection {:type "albersUsa"}
           :mark "geoshape"
           :encoding {:color {:field "rate"
                              :type "quantitative"}}})
