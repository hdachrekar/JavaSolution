/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.model;
import com.ecw.encryption.Aes128Kafka;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class KafkaConfig {

    @JsonProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG)
    private String bootstrapServers;

    @Value("${ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG:use_all_dns_ips}")
    @JsonProperty(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG)
    private String dnsLookUpConfig;

    @Value("${security.protocol:SASL_SSL}")
    @JsonProperty("security.protocol")
    private String securityProtocol;

    @JsonProperty("jaasTemplate")
    private String jaasTemplate;

    @JsonProperty("jaas.config.username")
    private String jaasConfigUsername;

    @JsonProperty("jaas.config.password")
    private String jaasConfigPassword;

    @Value("${sasl.mechanism:PLAIN}")
    @JsonProperty("sasl.mechanism")
    private String saslMechanism;

    @Value("${ConsumerConfig.AUTO_OFFSET_RESET_CONFIG:earliest}")
    @JsonProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)
    private String autoOffsetReset;

    @Value("${ConsumerConfig.FETCH_MIN_BYTES_CONFIG:1}")
    @JsonProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG)
    private String fetchMinBytes;

    @Value("${ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG:60000}")
    @JsonProperty(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG)
    private String defaultApiTimeoutMs;

    @Value("${ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG:false}")
    @JsonProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG)
    private String enableAutoCommit;

    @Value("${ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG:10000}")
    @JsonProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG)
    private String autoCommitIntervalMs;

    @Value("${ConsumerConfig.MAX_POLL_RECORDS_CONFIG:10}")
    @JsonProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG)
    private String maxPollRecords;

    @Value("${ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG:60000}")
    @JsonProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG)
    private String maxPollIntervalMs;

    @Value("${ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG:1000}")
    @JsonProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG)
    private String heartbeatIntervalMs;

    @Value("${ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG:30000}")
    @JsonProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG)
    private String sessionTimeoutMs;

    @Value("${ProducerConfig.ACKS_CONFIG:all}")
    @JsonProperty(ProducerConfig.ACKS_CONFIG)
    private String acks;

    @Value("${ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION:1}")
    @JsonProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION)
    private String maxFlightReqPerConnection;

    @Value("${ProducerConfig.RETRIES_CONFIG:3}")
    @JsonProperty(ProducerConfig.RETRIES_CONFIG)
    private String retries;

    @Value("${ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG:15000}")
    @JsonProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG)
    private String requestTimeoutMs;

    @Value("${ProducerConfig.RETRY_BACKOFF_MS_CONFIG:10000}")
    @JsonProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG)
    private String retryBackoffMs;

    @Value("${ProducerConfig.BATCH_SIZE_CONFIG:65536}")
    @JsonProperty(ProducerConfig.BATCH_SIZE_CONFIG)
    private String batchSize;

    @Value("${ProducerConfig.LINGER_MS_CONFIG:1}")
    @JsonProperty(ProducerConfig.LINGER_MS_CONFIG)
    private String lingerMs;

    @Value("${ProducerConfig.BUFFER_MEMORY_CONFIG:33554432}")
    @JsonProperty(ProducerConfig.BUFFER_MEMORY_CONFIG)
    private String bufferMemory;

    @JsonProperty(ConsumerConfig.GROUP_ID_CONFIG)
    private String groupId;

    @Value("${TopicConfig.RETENTION_MS_CONFIG:36000000}")
    @JsonProperty(TopicConfig.RETENTION_MS_CONFIG)
    private String adminRetentionConfig;

    @Value("${TopicConfig.CLEANUP_POLICY_CONFIG: TopicConfig.CLEANUP_POLICY_DELETE}")
    @JsonProperty(TopicConfig.CLEANUP_POLICY_CONFIG)
    private String adminCleanupPolicy;

    @JsonProperty("kafka.guid")
    private String guid;

    @JsonProperty("consumer.thread.capacity")
    private String consumerThreadCapacity ;

    @JsonProperty("consumer.threads")
    private String consumerThreads ;

    @JsonProperty("kafka.KafkaConnectionTimeout")
    private long kafkaConnectionTimeout;

    @JsonProperty("consumer.threads.maxlimit")
    private String consumerThreadsMaxlimit ;

    @JsonProperty("consumer.threads.keepalive")
    private String consumerThreadsKeepAlive;

    @JsonProperty("consumer.threads.polltimeoutinterval")
    private String consumerThreadsPollTimeoutInterval;

    @JsonProperty("producer.thread.capacity")
    private String producerThreadCapacity ;

    @JsonProperty("producer.threads")
    private String producerThreads ;

    @JsonProperty("producer.threads.maxlimit")
    private String producerThreadsMaxlimit ;

    @JsonProperty("producer.threads.keepalive")
    private String producerThreadsKeepAlive;

    @JsonProperty("producer.threads.allowCoreThreadTimeOut")
    private String producerThreadsAllowCoreThreadTimeOut;

    public String getBootstrapServers() {
        return bootstrapServers;
    }
    public void setBootstrapServers( String bootstrapServers) {
        this.bootstrapServers = Aes128Kafka.decrypt(bootstrapServers);
    }

    public String getGroupId() { return groupId;  }

    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getDnsLookUpConfig() {
        return dnsLookUpConfig;
    }

    public void setDnsLookUpConfig(String dnsLookUpConfig) {
        this.dnsLookUpConfig = dnsLookUpConfig;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = Aes128Kafka.decrypt(securityProtocol);
    }
    public String getJaasTemplate() {
        return jaasTemplate;
    }

    public void setJaasTemplate( String jaasTemplate){
        this.jaasTemplate = Aes128Kafka.decrypt(jaasTemplate);
        }

    public String getJaasConfigUsername() {
        return jaasConfigUsername;
    }

    public void setJaasConfigUsername( String jaasConfigUsername) {
        this.jaasConfigUsername = Aes128Kafka.decrypt(jaasConfigUsername);
    }

    public String getJaasConfigPassword() {
        return jaasConfigPassword;
    }

    public void setJaasConfigPassword( String jaasConfigPassword) {
        this.jaasConfigPassword = Aes128Kafka.decrypt(jaasConfigPassword);
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public String getFetchMinBytes() {
        return fetchMinBytes;
    }

    public void setFetchMinBytes(String fetchMinBytes) {
        this.fetchMinBytes = fetchMinBytes;
    }

    public String getDefaultApiTimeoutMs() {
        return defaultApiTimeoutMs;
    }

    public void setDefaultApiTimeoutMs(String defaultApiTimeoutMs) {
        this.defaultApiTimeoutMs = defaultApiTimeoutMs;
    }

    public String getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(String enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public String getAutoCommitIntervalMs() {
        return autoCommitIntervalMs;
    }

    public void setAutoCommitIntervalMs(String autoCommitIntervalMs) { this.autoCommitIntervalMs = autoCommitIntervalMs; }

    public String getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(String maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public String getMaxPollIntervalMs() {
        return maxPollIntervalMs;
    }

    public void setMaxPollIntervalMs(String maxPollIntervalMs) {
        this.maxPollIntervalMs = maxPollIntervalMs;
    }

    public String getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(String heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public String getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(String sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getMaxFlightReqPerConnection() {
        return maxFlightReqPerConnection;
    }

    public void setMaxFlightReqPerConnection(String maxFlightReqPerConnection) { this.maxFlightReqPerConnection = maxFlightReqPerConnection; }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(String requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public String getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(String retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(String lingerMs) {
        this.lingerMs = lingerMs;
    }

    public String getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(String bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public String getAdminRetentionConfig() {
        return adminRetentionConfig;
    }

    public void setAdminRetentionConfig(String adminRetentionConfig) {
        this.adminRetentionConfig = adminRetentionConfig;
    }

    public String getAdminCleanupPolicy() {
        return adminCleanupPolicy;
    }

    public void setAdminCleanupPolicy(String adminCleanupPolicy) {
        this.adminCleanupPolicy = adminCleanupPolicy;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getConsumerThreadCapacity() {
        return consumerThreadCapacity;
    }
    public void setConsumerThreadCapacity(String consumerThreadCapacity) {
        this.consumerThreadCapacity = consumerThreadCapacity;
    }

    public long getKafkaConnectionTimeout() {
        return kafkaConnectionTimeout;
    }

    public void setKafkaConnectionTimeout(long kafkaConnectionTimeout) {
        this.kafkaConnectionTimeout = kafkaConnectionTimeout;
    }

    public String getConsumerThreads(){return consumerThreads; }
    public void setConsumerThreads(String consumerThreads) {
        this.consumerThreads = consumerThreads;
    }

    public String getConsumerThreadsMaxlimit() {
        return consumerThreadsMaxlimit;
    }
    public void setConsumerThreadsMaxlimit(String consumerThreadsMaxlimit) {
        this.consumerThreadsMaxlimit = consumerThreadsMaxlimit;
    }

    public String getConsumerThreadsKeepAlive(){return consumerThreadsKeepAlive; }
    public void setConsumerThreadsKeepAlive(String consumerThreadsKeepAlive ){
        this.consumerThreadsKeepAlive = consumerThreadsKeepAlive;
    }

    public String getConsumerThreadsPollTimeoutInterval() {return consumerThreadsPollTimeoutInterval; }
    public void setConsumerThreadsPollTimeoutInterval(String consumerThreadsPollTimeoutInterval) {
        this.consumerThreadsPollTimeoutInterval = consumerThreadsPollTimeoutInterval;
    }

    public String getProducerThreadCapacity() {
        return producerThreadCapacity;
    }
    public void setProducerThreadCapacity(String producerThreadCapacity) {
        this.producerThreadCapacity = producerThreadCapacity;
    }

    public String getProducerThreads(){return producerThreads; }
    public void setProducerThreads(String producerThreads) {
        this.producerThreads = producerThreads;
    }

    public String getProducerThreadsMaxlimit() {
        return producerThreadsMaxlimit;
    }
    public void setProducerThreadsMaxlimit(String producerThreadsMaxlimit) {
        this.producerThreadsMaxlimit = producerThreadsMaxlimit;
    }

    public String getProducerThreadsKeepAlive(){return consumerThreadsKeepAlive; }
    public void setProducerThreadsKeepAlive(String producerThreadsKeepAlive ){
        this.producerThreadsKeepAlive = producerThreadsKeepAlive;
    }

    public String getProducerThreadsAllowCoreThreadTimeOut() {return producerThreadsAllowCoreThreadTimeOut; }
    public void setProducerThreadsAllowCoreThreadTimeOut(String producerThreadsPollTimeoutInterval) {
        this.producerThreadsAllowCoreThreadTimeOut = producerThreadsPollTimeoutInterval;
    }
}
