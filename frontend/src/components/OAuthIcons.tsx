/** 飞书 Logo SVG 图标 */
const FeishuIcon = ({ size = 24 }: { size?: number }) => (
  <svg width={size} height={size} viewBox="0 0 48 48" fill="none">
    <path
      d="M8.5 6C6.567 6 5 7.567 5 9.5v9.172c0 1.169.562 2.264 1.516 2.93L22.484 33.57c.954.666 2.078.666 3.032 0L41.484 21.602A3.478 3.478 0 0043 18.672V9.5C43 7.567 41.433 6 39.5 6h-31z"
      fill="#3370FF"
    />
    <path
      d="M5 28.828V38.5C5 40.433 6.567 42 8.5 42h31c1.933 0 3.5-1.567 3.5-3.5V28.828c0-1.169-.562-2.264-1.516-2.93L25.516 13.93a3.478 3.478 0 00-3.032 0L6.516 25.898A3.478 3.478 0 005 28.828z"
      fill="#3370FF"
      opacity="0.7"
    />
  </svg>
);

/** 企业微信 Logo SVG 图标 */
const WeComIcon = ({ size = 24 }: { size?: number }) => (
  <svg width={size} height={size} viewBox="0 0 48 48" fill="none">
    <path
      d="M24 4C12.954 4 4 12.954 4 24s8.954 20 20 20 20-8.954 20-20S35.046 4 24 4z"
      fill="#07C160"
    />
    <path
      d="M16.5 18a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0zM26.5 18a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0zM36.5 18a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0zM36.5 28a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z"
      fill="white"
    />
    <path
      d="M15 26c0-2 2-6 9-6s9 4 9 6-2 6-9 6-9-4-9-6z"
      fill="white"
      opacity="0.9"
    />
  </svg>
);

/** 微信 Logo SVG 图标 */
const WechatIcon = ({ size = 24 }: { size?: number }) => (
  <svg width={size} height={size} viewBox="0 0 48 48" fill="none">
    <path
      d="M24 4C12.954 4 4 12.954 4 24s8.954 20 20 20 20-8.954 20-20S35.046 4 24 4z"
      fill="#07C160"
    />
    <path
      d="M17.5 20a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM26.5 20a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z"
      fill="white"
    />
    <path
      d="M13 28c0-3.5 4-7 11-7s11 3.5 11 7-4 7-11 7c-1.5 0-3-.2-4.3-.5L15 36l1.2-3.3C14.2 31.2 13 29.7 13 28z"
      fill="white"
      opacity="0.95"
    />
  </svg>
);

export { FeishuIcon, WeComIcon, WechatIcon };
