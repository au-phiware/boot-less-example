(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure    "1.8.0"]
                          [deraen/boot-less       "0.6.2"      :scope "test"]
                          [org.slf4j/slf4j-nop    "1.7.25"     :scope "test"]
                          [org.webjars/bootstrap  "3.3.7-1"]
                          [org.webjars/bootswatch "3.3.7"]])

(require '[deraen.boot-less      :refer [less]])

(task-options! less {:source-map true})
