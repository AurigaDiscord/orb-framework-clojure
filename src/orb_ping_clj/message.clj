(ns orb-framework.message
  (:require [clojure.data.json :as json]))

(defn text
  [channel-id body]
  (json/write-str {:type "text"
                   :channel_id channel-id
                   :content body}))
