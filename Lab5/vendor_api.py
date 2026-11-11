"""Stub of the machine manufacturer's service catalogue.

Run: python3 vendor_api.py            (listens on http://localhost:8090)
     python3 vendor_api.py 8091       (another port)

GET /api/maintenance-plans/{machineType} returns the recommended maintenance intervals.
Some machine types misbehave on purpose: see RESPONSES below.
"""
import json
import sys
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

PREFIX = "/api/maintenance-plans/"

# machine type -> (HTTP status, body, delay in seconds)
RESPONSES = {
    "CNC_LATHE": (200, {"machineType": "CNC_LATHE", "inspectionIntervalDays": 30, "preventiveIntervalDays": 90}, 0),
    "MILLING_MACHINE": (200, {"machineType": "MILLING_MACHINE", "inspectionIntervalDays": 45, "preventiveIntervalDays": 120}, 0),
    "HYDRAULIC_PRESS": (200, {"machineType": "HYDRAULIC_PRESS", "inspectionIntervalDays": 14, "preventiveIntervalDays": 60}, 0),
    "INJECTION_MOLDER": (200, {"machineType": "INJECTION_MOLDER", "inspectionIntervalDays": 30, "preventiveIntervalDays": 75}, 0),
    "WELDING_ROBOT": (200, {"machineType": "WELDING_ROBOT", "inspectionIntervalDays": 21, "preventiveIntervalDays": 45}, 10),
    "CONVEYOR": (500, {"error": "catalogue database unavailable"}, 0),
}


class Handler(BaseHTTPRequestHandler):

    def do_GET(self):
        if not self.path.startswith(PREFIX):
            self.reply(404, {"error": "no such endpoint"})
            return
        machine_type = self.path[len(PREFIX):]
        status, body, delay = RESPONSES.get(machine_type, (404, {"error": "unknown machine type: " + machine_type}, 0))
        time.sleep(delay)
        self.reply(status, body)

    def reply(self, status, body):
        data = json.dumps(body).encode("utf-8")
        try:
            self.send_response(status)
            self.send_header("Content-Type", "application/json")
            self.send_header("Content-Length", str(len(data)))
            self.end_headers()
            self.wfile.write(data)
        except (BrokenPipeError, ConnectionResetError):
            pass  # the client gave up waiting (read timeout)


if __name__ == "__main__":
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 8090
    print(f"Vendor API on http://localhost:{port}{PREFIX}<machineType>")
    ThreadingHTTPServer(("", port), Handler).serve_forever()