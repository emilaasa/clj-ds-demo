(ns emilaasa.clj-ds-demo
  (:require [nextjournal.clerk :as clerk]
            [scicloj.ml.core :as ml]
            [scicloj.ml.metamorph :as mm]
            [scicloj.ml.dataset :as ds]))

;; A this is how we start clerk. Having these commands outside a comment block will cause an infinite loop.
(comment
  (clerk/serve! {:browse? true
                 :watch-paths ["src"]})
  (clerk/show! "src/emilaasa/clj_ds_demo.clj"))

;; # Markdown
;; Full __support__ for [markdown](https://codescene.io)
;; |  this  |  be   |
;; |--------|-------|
;; |  could |  cool |
#_(clerk/plotly {:data [{:z [[1 2 3] [3 2 1]] :type "surface"}]})
#_(clerk/vl {:width 650
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

;; # Where's the data
;; I've chosen to work with some open source data here, and for convenience I'll go with scicloj's own examples.
;; The point is to show the tooling in action.  
;; First let's introduce the training & testing data:
(def titanic-train
  (->
    (ds/dataset "https://github.com/scicloj/metamorph-examples/raw/main/data/titanic/train.csv"
                {:key-fn keyword})))
 
#_(def titanic-test
  (->
   (ds/dataset "https://github.com/scicloj/metamorph-examples/raw/main/data/titanic/test.csv"
               {:key-fn keyword
                :parser-fn :string})
   (ds/add-column :Survived [""] :cycle)))


(def pipe-fn
    (ml/pipeline
        (mm/select-columns [:Survived :Pclass])
        (mm/add-column :Survived (fn [ds] (map #(case % "1" "yes" "0" "no" nil "") (:Survived ds))))
        (mm/categorical->number [:Survived :Pclass])
        (mm/set-inference-target :Survived)
        {:metamorph/id :model}
        (mm/model {:model-type :smile.classification/logistic-regression})))

(def trained-ctx
    (pipe-fn {:metamorph/data titanic-train
              :metamorph/mode :fit}))

;; I'm getting macro expansion errors here, wonder why:
;; ```clojure
;;  (def test-ctx
;;   (pipe-fn
;;    (assoc trained-ctx
;;           :metamorph/data titanic-test
;;           :metamorph/mode :transform)))
;; ```

#_(-> test-ctx :metamorph/data
    (ds/column-values->categorical :Survived))

(ds/shape titanic-train)

(clerk/table [(keys titanic-train) (vec (take 10 (ds/rows titanic-train)))])



