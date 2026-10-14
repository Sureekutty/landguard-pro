-- LandGuard Pro - Production PostgreSQL Schema Migration (V1)
-- Compatible with PostgreSQL 14+ and H2 PostgreSQL Mode

CREATE TABLE IF NOT EXISTS app_users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS source_registry (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    official_url VARCHAR(255) NOT NULL,
    jurisdiction VARCHAR(100) NOT NULL,
    supported_record_types VARCHAR(255) NOT NULL,
    supported_search_fields VARCHAR(255) NOT NULL,
    integration_method VARCHAR(50) NOT NULL,
    authorization_status VARCHAR(50) NOT NULL,
    integration_status VARCHAR(50) NOT NULL,
    last_successful_retrieval TIMESTAMP,
    last_health_check TIMESTAMP,
    known_limitations TEXT,
    applicable_usage_restrictions TEXT,
    source_disclaimer TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS source_retrieval_history (
    id VARCHAR(36) PRIMARY KEY,
    source_id VARCHAR(50) NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    status_code INT,
    response_time_ms BIGINT,
    success BOOLEAN NOT NULL,
    error_message VARCHAR(500),
    retrieval_timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (source_id) REFERENCES source_registry(id)
);

CREATE TABLE IF NOT EXISTS property_search_requests (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    district VARCHAR(100),
    taluk VARCHAR(100),
    village VARCHAR(100),
    survey_number VARCHAR(50),
    subdivision VARCHAR(50),
    patta_number VARCHAR(50),
    owner_name VARCHAR(150),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS retrieved_property_records (
    id VARCHAR(36) PRIMARY KEY,
    search_id VARCHAR(36),
    source_id VARCHAR(50),
    district VARCHAR(100) NOT NULL,
    taluk VARCHAR(100) NOT NULL,
    village VARCHAR(100) NOT NULL,
    survey_number VARCHAR(50) NOT NULL,
    subdivision VARCHAR(50),
    full_survey VARCHAR(100) NOT NULL,
    patta_number VARCHAR(50),
    owner_name_raw VARCHAR(255) NOT NULL,
    owner_name_normalized VARCHAR(255),
    father_name VARCHAR(255),
    land_classification VARCHAR(100),
    extent_raw VARCHAR(100),
    extent_sqft DOUBLE PRECISION,
    extent_cents DOUBLE PRECISION,
    asking_rate DOUBLE PRECISION,
    guideline_rate DOUBLE PRECISION,
    dtcp_approved BOOLEAN DEFAULT FALSE,
    dtcp_status VARCHAR(255),
    ec_clean BOOLEAN DEFAULT FALSE,
    ec_status VARCHAR(255),
    is_demo_data BOOLEAN NOT NULL DEFAULT FALSE,
    evidence_status VARCHAR(100),
    evidence_citation TEXT,
    retrieval_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS property_comparisons (
    id VARCHAR(36) PRIMARY KEY,
    property_id VARCHAR(36),
    overall_verdict VARCHAR(50) NOT NULL,
    survey_match VARCHAR(30) NOT NULL,
    owner_match VARCHAR(30) NOT NULL,
    extent_match VARCHAR(30) NOT NULL,
    encumbrance_status VARCHAR(50) NOT NULL,
    comparison_summary TEXT,
    comparison_details_json TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_dossiers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    district VARCHAR(100) NOT NULL,
    taluk VARCHAR(100) NOT NULL,
    village VARCHAR(100) NOT NULL,
    survey_number VARCHAR(50) NOT NULL,
    subdivision VARCHAR(50),
    claimed_owner VARCHAR(255),
    extent_sqft DOUBLE PRECISION,
    asking_rate DOUBLE PRECISION,
    guideline_rate DOUBLE PRECISION,
    dtcp_approved BOOLEAN,
    ec_clean BOOLEAN,
    status_notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS audit_events (
    id VARCHAR(36) PRIMARY KEY,
    actor_username VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(100),
    ip_address VARCHAR(50),
    details TEXT,
    timestamp TIMESTAMP NOT NULL
);

-- Seed Initial Official Tamil Nadu Government Sources in Registry
INSERT INTO source_registry (
    id, name, official_url, jurisdiction, supported_record_types, supported_search_fields,
    integration_method, authorization_status, integration_status, known_limitations,
    applicable_usage_restrictions, source_disclaimer, created_at, updated_at
) VALUES 
(
    'anytn-eservices',
    'AnyTN e-Services Land Records (Revenue Department)',
    'https://eservices.tn.gov.in/',
    'Tamil Nadu (All 38 Districts)',
    'Patta Extract, Chitta Extract, FMB Sketch, TSLR Extract, A-Register',
    'District, Taluk, Village, Survey Number, Subdivision, Patta Number',
    'MANUAL_AND_HEALTH_CHECK',
    'PUBLIC_WEB_CAPTCHA_REQUIRED',
    'MANUAL_VERIFICATION',
    'Public queries require interactive Tamil CAPTCHA solving. Automated robotic queries prohibited by NIC terms of service.',
    'Informational extract for citizen verification only. Certified copies must be obtained from Taluk Tahsildar.',
    'Official revenue records maintained by Department of Survey and Settlement, Government of Tamil Nadu.',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'tnreginet-ec',
    'TNREGINET (Inspector General of Registration)',
    'https://tnreginet.gov.in/',
    'Tamil Nadu (Registration Department)',
    'Encumbrance Certificate (EC Form 15/16), Guideline Value, Registered Document Search',
    'Zone, District, SRO, Village, Survey Number, Subdivision, Document Number, Year',
    'MANUAL_AND_HEALTH_CHECK',
    'PUBLIC_WEB_CAPTCHA_REQUIRED',
    'MANUAL_VERIFICATION',
    'Interactive user login and CAPTCHA required for non-watermarked certified copies. Electronic EC search available online.',
    'Online view copy is non-certified. Certified copies require application at concerned Sub-Registrar Office.',
    'Registration data maintained by Registration Department, Government of Tamil Nadu.',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'dtcp-onlineppa',
    'DTCP / OnlinePPA (Directorate of Town & Country Planning)',
    'https://onlineppa.tn.gov.in/',
    'Tamil Nadu (Excluding CMDA area)',
    'Layout Sanction Status, Plot Regularisation Scheme (G.O. 78/172), Planning Permission',
    'Local Body Type, District, Taluk, Village, Survey Number, Layout Approval Number',
    'MANUAL_AND_HEALTH_CHECK',
    'PUBLIC_SEARCHABLE',
    'MANUAL_VERIFICATION',
    'Requires layout identification or local body inward number for detailed regularisation order copies.',
    'Under Section 22-A of Registration Act, resale of unapproved layouts is barred without regularisation order.',
    'Maintained by Directorate of Town and Country Planning, Government of Tamil Nadu.',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'tnglms',
    'Tamil Nadu Government Land Management System (TNGLMS)',
    'https://tnglms.in/',
    'Tamil Nadu (State-wide Government Lands)',
    'Government Land Registry, Prohibited Properties, Poramboke & Waterbody Demarcation',
    'District, Taluk, Village, Survey Number',
    'HEALTH_MONITORED',
    'DEPARTMENTAL_RESTRICTED',
    'REQUIRES_AUTHORIZATION',
    'Full cadastral mutation database is restricted to authorized Revenue officials.',
    'Government property classification check requires official revenue nodal officer authorization.',
    'Maintained by Tamil Nadu Revenue and Disaster Management Department.',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'ecourts-tn',
    'e-Courts Services (Judicial Case Information System)',
    'https://services.ecourts.gov.in/',
    'Namakkal / Rasipuram & Tamil Nadu District Judiciary',
    'Civil Suit Pending Status, Interim Injunction Orders, Execution Petitions',
    'State, District, Court Complex, Case Type, Case Number, Year, Party Name',
    'HEALTH_MONITORED',
    'PUBLIC_CAPTCHA_REQUIRED',
    'MANUAL_VERIFICATION',
    'Requires party name or case number and interactive CAPTCHA.',
    'Case status information is for due diligence only. Legal title opinions must be issued by a practicing advocate.',
    'Maintained by e-Committee, Supreme Court of India & Ministry of Law and Justice.',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
