Proposal for handling errors:
1. Send errors back to the front clients error messages
2. Send errors to a error route, and if this fails then events will be dropped
3. Send ack for each event received through websocket, the ack will be sent once all outbounds destination were successful. This strategy can be combined with the error route