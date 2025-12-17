-- Откат миграции v1.5

DROP TRIGGER IF EXISTS trigger_audit_user_role_delete ON user_roles;
DROP TRIGGER IF EXISTS trigger_audit_user_role_insert ON user_roles;
DROP TRIGGER IF EXISTS trigger_audit_order_status ON orders;

DROP FUNCTION IF EXISTS audit_user_role_changes();
DROP FUNCTION IF EXISTS audit_order_status_change();

DROP TABLE IF EXISTS audit_log;
