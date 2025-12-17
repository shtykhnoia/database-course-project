CREATE TABLE audit_log
(
    id          BIGSERIAL PRIMARY KEY,
    table_name  VARCHAR(50) NOT NULL,
    operation   VARCHAR(10) NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    record_id   BIGINT      NOT NULL,
    old_data    JSONB,
    new_data    JSONB,
    changed_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);

CREATE OR REPLACE FUNCTION audit_order_status_change()
    RETURNS TRIGGER AS
$$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_data, new_data, description)
        VALUES ('orders',
                'UPDATE',
                NEW.id,
                jsonb_build_object(
                        'status', OLD.status,
                        'order_number', OLD.order_number,
                        'total_amount', OLD.total_amount,
                        'user_id', OLD.user_id
                ),
                jsonb_build_object(
                        'status', NEW.status,
                        'order_number', NEW.order_number,
                        'total_amount', NEW.total_amount,
                        'user_id', NEW.user_id
                ),
                'Order #' || NEW.order_number || ' status: ' || OLD.status || ' → ' || NEW.status);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_audit_order_status
    AFTER UPDATE
    ON orders
    FOR EACH ROW
EXECUTE FUNCTION audit_order_status_change();

CREATE OR REPLACE FUNCTION audit_user_role_changes()
    RETURNS TRIGGER AS
$$
DECLARE
    role_name VARCHAR(50);
BEGIN
    IF TG_OP = 'INSERT' THEN
        SELECT name INTO role_name FROM roles WHERE id = NEW.role_id;
        INSERT INTO audit_log (table_name, operation, record_id, new_data, description)
        VALUES ('user_roles',
                'INSERT',
                NEW.user_id,
                jsonb_build_object(
                        'user_id', NEW.user_id,
                        'role_id', NEW.role_id,
                        'role_name', role_name
                ),
                'Role "' || role_name || '" assigned to user #' || NEW.user_id);
    ELSIF TG_OP = 'DELETE' THEN
        SELECT name INTO role_name FROM roles WHERE id = OLD.role_id;
        INSERT INTO audit_log (table_name, operation, record_id, old_data, description)
        VALUES ('user_roles',
                'DELETE',
                OLD.user_id,
                jsonb_build_object(
                        'user_id', OLD.user_id,
                        'role_id', OLD.role_id,
                        'role_name', role_name
                ),
                'Role "' || role_name || '" removed from user #' || OLD.user_id);
        RETURN OLD;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_audit_user_role_insert
    AFTER INSERT
    ON user_roles
    FOR EACH ROW
EXECUTE FUNCTION audit_user_role_changes();

CREATE TRIGGER trigger_audit_user_role_delete
    AFTER DELETE
    ON user_roles
    FOR EACH ROW
EXECUTE FUNCTION audit_user_role_changes();
