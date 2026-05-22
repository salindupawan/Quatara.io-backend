-- Enable extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Trigger function to auto-update updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- 1. Organizations
CREATE TABLE organizations (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               name VARCHAR(255) NOT NULL,
                               clerk_org_id VARCHAR(255) UNIQUE,
                               created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_org_modtime BEFORE UPDATE ON organizations FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();

-- 2. Users
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       clerk_id VARCHAR(255) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       organization_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_clerk_id ON users(clerk_id);
CREATE INDEX idx_users_org_id ON users(organization_id);

-- 3. Projects
CREATE TABLE projects (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          client_name VARCHAR(255) NOT NULL,
                          client_email VARCHAR(255) NOT NULL,
                          project_name VARCHAR(255) NOT NULL,
                          deposit_amount DECIMAL(10, 2) NOT NULL,
                          organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_projects_modtime BEFORE UPDATE ON projects FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE INDEX idx_projects_org_id ON projects(organization_id);
CREATE INDEX idx_projects_client_email ON projects(client_email);

-- 4. Documents
CREATE TABLE documents (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
                           status VARCHAR(50) DEFAULT 'draft',
                           pdf_url TEXT,
                           created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_docs_modtime BEFORE UPDATE ON documents FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE INDEX idx_documents_project_id ON documents(project_id);
CREATE INDEX idx_documents_status ON documents(status);

-- 5. Annotations (Coordinates)
CREATE TABLE annotations (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             document_id UUID REFERENCES documents(id) ON DELETE CASCADE,
                             page_index INTEGER NOT NULL,
                             x_coordinate FLOAT8 NOT NULL,
                             y_coordinate FLOAT8 NOT NULL,
                             annotation_type VARCHAR(50) NOT NULL,
                             created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_annotations_doc_id ON annotations(document_id);

-- 6. Payments
CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          stripe_intent_id VARCHAR(255) UNIQUE NOT NULL,
                          amount_cents BIGINT NOT NULL,
                          currency VARCHAR(10) DEFAULT 'usd',
                          status VARCHAR(50),
                          user_id UUID REFERENCES users(id) ON DELETE SET NULL,
                          document_id UUID REFERENCES documents(id) ON DELETE SET NULL,
                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_stripe_id ON payments(stripe_intent_id);
CREATE INDEX idx_payments_doc_id ON payments(document_id);

-- 7. Payment Links
CREATE TABLE payment_links (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
                               short_code VARCHAR(50) UNIQUE NOT NULL,
                               stripe_link_url TEXT NOT NULL,
                               amount_cents BIGINT NOT NULL,
                               is_active BOOLEAN DEFAULT TRUE,
                               expires_at TIMESTAMPTZ,
                               view_count INTEGER DEFAULT 0,
                               last_viewed_at TIMESTAMPTZ,
                               created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_paylinks_modtime BEFORE UPDATE ON payment_links FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE INDEX idx_paylinks_short_code ON payment_links(short_code);
CREATE INDEX idx_paylinks_project_id ON payment_links(project_id);

-- 8. Signatures (Audit Trail)
CREATE TABLE signatures (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            document_id UUID REFERENCES documents(id) ON DELETE CASCADE,
                            signer_email VARCHAR(255) NOT NULL,
                            signed_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                            ip_address VARCHAR(45),
                            signature_data TEXT,
                            created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_signatures_doc_id ON signatures(document_id);
