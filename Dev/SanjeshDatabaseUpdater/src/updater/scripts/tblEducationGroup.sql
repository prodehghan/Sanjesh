CREATE TABLE educationgroup
(
  educationgroup_id SERIAL NOT NULL,
  code character varying(50) NOT NULL,
  "name" character varying(255) NOT NULL,
  CONSTRAINT educationgroup_pkey PRIMARY KEY (educationgroup_id),
  CONSTRAINT educationgroup_name_key UNIQUE ("name")
)
WITH (
  OIDS=FALSE
);