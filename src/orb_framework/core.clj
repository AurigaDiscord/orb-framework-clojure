(ns orb-framework.core
  (:require [taoensso.timbre       :as log]
            [clojure.core.async    :refer [go]]
            [environ.core          :refer [env]]
            [langohr.core          :as rmq]
            [langohr.channel       :as lch]
            [langohr.queue         :as lq]
            [langohr.consumers     :as lc]
            [langohr.basic         :as lb]
            [clojure.data.json     :as json]
            [orb-framework.metrics :as metrics]))

(log/set-level! :info)

(def config (atom {}))

(defn- load-config-from-env
  [prefix]
  (swap! config merge {:amqp-path (env :amqp-path)
                       :amqp-exchange (format "%s.%s" prefix (env :amqp-exchange))
                       :amqp-key-message (format "%s.%s" prefix (env :amqp-key-message))
                       :amqp-key-outcoming (format "%s.%s" prefix (env :amqp-key-outcoming))
                       :amqp-queue-incoming (format "%s.%s.%s" prefix (env :amqp-exchange) (env :amqp-queue-incoming))}))

(defn- wrap-handler
  [handler]
  (fn [ch meta ^bytes raw-payload]
    (let [message (json/read-str (String. raw-payload "UTF-8") :key-fn keyword)
          result (handler message)]
      (if (some? result)
        (lb/publish ch (@config :amqp-exchange) (@config :amqp-key-outcoming) result)))))

(defn run
  ([name handler]
    (run name handler "auriga"))
  ([name handler amqp-prefix]
    (load-config-from-env amqp-prefix)
    (log/infof "Starting %s orb." name)
    (go (metrics/start))
    (let [conn  (rmq/connect (rmq/settings-from (@config :amqp-path)))
          ch    (lch/open conn)
          qname (@config :amqp-queue-incoming)]
      (log/info (format "Connected to AMQP. Channel id: %d" (.getChannelNumber ch)))
      (lq/declare ch qname {:exclusive false :auto-delete true})
      (lq/bind ch qname (@config :amqp-exchange) {:routing-key (@config :amqp-key-message)})
      (lc/subscribe ch qname (wrap-handler handler) {:auto-ack true}))))
