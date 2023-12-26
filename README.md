# Huk Wink

HukWink, 是一个在全平台运行, 提供多平台机器人对接支持的机器人框架库

> [!NOTE]
>
> Work In Progress

## 许可证

```text
Copyright (C) 2022-2024 KasukuSakura Technologies and contributors.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

## 协议支持

<table>
<tr><th>Platform Larksuite (Feishu) </th></tr>
<tr><td>

| 模块 <!-- PADDING  --> | 能力 <!-- PADDING     --> | <!-- PADDING           --> | 支持状态  <!-- PADDING         --> |
|:---------------------|:------------------------|:---------------------------|:-------------------------------|
| 消息                   | 接受消息                    | 纯文本                        | Y                              |
|                      |                         | @At                        | Y                              |
|                      |                         | 消息回复                       | Y                              |
|                      |                         | 表情包                        | 平台限制，只能获取其 key                 |
|                      |                         | 消息 ID                      | Y                              |
|                      | 发送消息                    | 获取发出消息 ID                  | Y                              |
|                      |                         | 纯文本                        | Y                              |
|                      |                         | @At                        | Y                              |
|                      |                         | 图片                         | Y                              |
|                      | 消息序列化                   | ALL                        | Y                              |
| 资源                   | 下载                      | 接受消息中图片下载                  | Y                              |
|                      | 上传                      | 上传图片并发送                    | Y                              |
|                      |                         | 发送文件                       | Y                              |

</td></tr>
</table>
