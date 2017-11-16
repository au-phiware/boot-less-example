(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure    "1.8.0"]
                          [degree9/boot-npm       "1.6.1"      :scope "test"]
                          [degree9/boot-exec      "1.0.0"      :scope "test"]
                          [org.webjars/bootstrap  "3.3.7-1"]
                          [org.webjars/bootswatch "3.3.7"]])

(require '[degree9.boot-npm :as npm :refer [npm]]
         '[degree9.boot-exec        :refer [exec]]
         '[clojure.java.io :as io])

(task-options!
  npm {:managed true
       :install {:less "2.7.2"
                 :less-plugin-clean-css "1.5.1"
                 :bootstrap "3.3.7"
                 :bootswatch "3.3.7"}})

(deftask
  npm-install
  "Download node modules."
  []
  (comp
    (npm)
    (sift :include [#"^node_modules/"])
    (target :no-clean true :dir #{"."})))

(deftask lessc-h
  "Compile less sources."
  []
  (fn [next]
    (fn [fs] (((npm/exec :module "less" :process "lessc" :arguments ["-h"]) next) fs))))

(deftask lessc
  "Compile less sources."
  []
  (fn [next]
    (fn [fs]
      (let [tmp (tmp-dir!)
            in  (.getAbsolutePath (tmp-file (tmp-get fs "main.less")))
            dir "node_modules"
            out (.getAbsolutePath (io/file tmp "main.css"))]
        (((exec :process "lessc" :arguments [in out] :directory dir :local "node_modules/less/bin")
          (fn [_]  (next (commit! (add-resource fs tmp)))))
         fs)))))

(deftask dev
  "Start developing!"
  []
  (comp
    (fn [next] (fn [fs] (((npm-install) (fn [_] (next fs))) fs)))
    (lessc)
    (target)))
