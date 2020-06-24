### Resources
- https://medium.com/@valyala/promql-tutorial-for-beginners-9ab455142085
- https://prometheus.io/docs/prometheus/latest/querying/basics/

### Queries
- rate of inbound events across all endpoints - sum(rate(gateway_inbound_received_total[5m])) by (app, pod, instance)
- top 10 endpoints by number of inbound events - sum(topk(10, gateway_inbound_received_total)) by (app, endpoint_path)

