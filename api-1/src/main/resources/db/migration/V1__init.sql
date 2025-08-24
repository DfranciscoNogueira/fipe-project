CREATE TABLE IF NOT EXISTS brand (
  id BIGSERIAL PRIMARY KEY,
  vehicle_type VARCHAR(20) NOT NULL,
  code VARCHAR(32) NOT NULL,
  name VARCHAR(120) NOT NULL,
  CONSTRAINT uk_brand_type_code UNIQUE (vehicle_type, code)
);

CREATE TABLE IF NOT EXISTS vehicle_model (
  id BIGSERIAL PRIMARY KEY,
  brand_id BIGINT NOT NULL REFERENCES brand(id) ON DELETE CASCADE,
  code VARCHAR(32) NOT NULL,
  name VARCHAR(160) NOT NULL,
  observations TEXT,
  CONSTRAINT uk_model_brand_code UNIQUE (brand_id, code)
);

CREATE INDEX IF NOT EXISTS idx_brand_name ON brand(name);
CREATE INDEX IF NOT EXISTS idx_model_name ON vehicle_model(name);
