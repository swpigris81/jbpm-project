
INSERT IGNORE INTO `menubutton` (`button_id`, `button_name`, `button_text`, `menu_id`, `button_url`, `button_show`, `button_css`, `handler`) VALUES
('002085543d39abf0013d39aeaa9c0002', 'pn_server_user_list', '用户列表', '002085543d39abf0013d39ad471e0001', '/pnserver/pnserverUserList.action?method=userList', 'no', 'none', ''),
('002085543d39abf0013d39af6b0c0003', 'pn_server_session_list', '会话列表', '002085543d39abf0013d39ad471e0001', '/pnserver/pnserverSessionList.action?method=sessionList', 'no', 'none', ''),
('002085543d39abf0013d39b099c30004', 'pn_server_notification_btn', '发送消息给Android', '002085543d39abf0013d39ad471e0001', '/pnserver/pnserverNotificationSend.action?method=notificationSend', 'no', 'table_edit', ''),

INSERT IGNORE INTO `menu_info` (`menu_id`, `menu_name`, `page_path`, `menu_level`, `parent_menu`, `is_leave`, `is_show`, `comment`) VALUES
('002085543d39abf0013d39ad471e0001', 'Android云推送', '/pnserver/pnserverBegin.action?method=begin', '', '4028098136ce4d040136ce4de4490001', '1', '1', NULL),
	