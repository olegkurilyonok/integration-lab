import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  scenarios: {
    spike_write: {
      executor: "constant-vus",
      vus: 200,
      duration: "2m",
    },
    steady_write: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "30s", target: 50 },
        { duration: "2m", target: 150 },
        { duration: "30s", target: 0 },
      ],
      startTime: "0s",
    },
  },
  thresholds: {
    http_req_duration: ["p(95)<800"],
    http_req_failed: ["rate<0.02"],
  },
};

export default function () {
  const url = "http://localhost:8080/messages";
  const payload = JSON.stringify({ message: "heavy-load" });
  const params = { headers: { "Content-Type": "application/json" } };

  const res = http.post(url, payload, params);
  check(res, { "status is 2xx": (r) => r.status >= 200 && r.status < 300 });
  sleep(0.05);
}
