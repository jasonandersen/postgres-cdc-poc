# Change Data Capture
## Proof of Concept
### Hypothesis
We have a legacy system with a significant amount of tightly-coupled cross-domain code and data that is high-risk. We need to start moving over some functionality to new services and applications as well as integrate 3rd party systems with the legacy system.

#### Change Data Capture
We believe we can communicate state changes in our legacy system out to external components or systems without modifying code in our legacy system. By using CDC patterns, specifically an `outbox` pattern, we can see data mutation events as they happen.

#### Outbox