(ns orb-framework.metrics
	(:require [prometheus.core        :as prometheus]
						[compojure.core         :refer :all]
            [ring.server.standalone :refer [serve]]
            [environ.core           :refer [env]]
            [taoensso.timbre        :as log]))

(defonce store (atom nil))

(defroutes handler
	(GET "/" []
		"ok")
	(GET "/metrics" []
		(prometheus/dump-metrics (:registry @store))))

(defn start [&]
	(log/info "Starting HTTP endpoint")
	(->> (prometheus/init-defaults)
       (reset! store))
	(serve
	  (prometheus/instrument-handler handler
	                                 "orb"
	                                 (:registry @store))
	  {:port (env :http-internal-port 13931) :open-browser? false}))
