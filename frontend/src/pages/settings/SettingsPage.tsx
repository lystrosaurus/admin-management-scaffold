import { useState } from 'react';
import {
  Card,
  Form,
  Input,
  InputNumber,
  Button,
  Select,
  Descriptions,
  message,
  Divider,
} from 'antd';
import { SaveOutlined } from '@ant-design/icons';

/** 语言选项 */
const languageOptions = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' },
];

/** 系统配置页面 */
const SettingsPage = () => {
  const [form] = Form.useForm();
  const [saving, setSaving] = useState(false);

  // Mock 系统基本信息（只读）
  const systemInfo = {
    name: 'Admin Management Scaffold',
    version: '1.0.0',
    environment: 'Production',
    javaVersion: '21',
    springBootVersion: '4.0.6',
    nodeVersion: '20.x',
  };

  // Mock 可编辑配置
  const [config, setConfig] = useState({
    siteName: 'Admin Management',
    language: 'zh-CN',
    loginFailLockCount: 5,
    sessionTimeout: 30,
    fileUploadMaxSize: 10,
  });

  /**
   * 处理保存配置
   */
  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);

      // 模拟保存（后续接入后端 API）
      await new Promise((resolve) => setTimeout(resolve, 500));

      setConfig(values);
      message.success('保存成功');
    } catch (error) {
      if ((error as { errorFields?: unknown }).errorFields) {
        return;
      }
      message.error((error as Error).message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      {/* 系统基本信息（只读） */}
      <Card title="系统信息" style={{ marginBottom: 16 }}>
        <Descriptions column={2} bordered size="small">
          <Descriptions.Item label="系统名称">{systemInfo.name}</Descriptions.Item>
          <Descriptions.Item label="版本号">{systemInfo.version}</Descriptions.Item>
          <Descriptions.Item label="运行环境">{systemInfo.environment}</Descriptions.Item>
          <Descriptions.Item label="Java 版本">{systemInfo.javaVersion}</Descriptions.Item>
          <Descriptions.Item label="Spring Boot 版本">{systemInfo.springBootVersion}</Descriptions.Item>
          <Descriptions.Item label="Node.js 版本">{systemInfo.nodeVersion}</Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 可编辑配置 */}
      <Card title="系统配置">
        <Form
          form={form}
          layout="vertical"
          initialValues={config}
          style={{ maxWidth: 500 }}
        >
          <Form.Item
            name="siteName"
            label="系统名称"
            rules={[{ required: true, message: '请输入系统名称' }]}
          >
            <Input placeholder="请输入系统名称" />
          </Form.Item>
          <Form.Item
            name="language"
            label="默认语言"
            rules={[{ required: true, message: '请选择默认语言' }]}
          >
            <Select options={languageOptions} placeholder="请选择默认语言" />
          </Form.Item>
          <Form.Item
            name="loginFailLockCount"
            label="登录失败锁定次数"
            rules={[{ required: true, message: '请输入登录失败锁定次数' }]}
            extra="连续登录失败达到此次数后，账户将被临时锁定"
          >
            <InputNumber
              min={1}
              max={20}
              placeholder="请输入次数"
              style={{ width: '100%' }}
            />
          </Form.Item>
          <Form.Item
            name="sessionTimeout"
            label="会话超时时间（分钟）"
            rules={[{ required: true, message: '请输入会话超时时间' }]}
            extra="用户无操作后，会话将在指定时间后过期"
          >
            <InputNumber
              min={5}
              max={1440}
              placeholder="请输入分钟数"
              style={{ width: '100%' }}
            />
          </Form.Item>
          <Form.Item
            name="fileUploadMaxSize"
            label="文件上传大小限制（MB）"
            rules={[{ required: true, message: '请输入文件上传大小限制' }]}
            extra="单个文件上传的最大大小"
          >
            <InputNumber
              min={1}
              max={100}
              placeholder="请输入 MB 数"
              style={{ width: '100%' }}
            />
          </Form.Item>
          <Divider />
          <Form.Item>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              onClick={handleSave}
              loading={saving}
            >
              保存配置
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default SettingsPage;
