oauth2-with-spring-security
===========================

# クライアント（ポート番号=8080）
- [client-jwt](./client-jwt)

> api-gateway-jwtにアクセスする場合は、main()内のコメントを外すか、実行時プロファイルとして `use-api-gateway` を指定してください。

# リソースサーバー（ポート番号=8090）
- [resource-server-jwt](./resource-server-jwt) - アクセストークンとしてJWTを使う場合の例
- [resource-server-opaque](./resource-server-opaque) - Token Introspectionを使う例

# API Gateway（ポート番号=8081）
- [api-gateway-jwt](./api-gateway-jwt)

# 認可サーバー（ポート番号=9000）
[こちらの記事](https://qiita.com/suke_masa/items/6b84826df81c083b384c)を参考に、Keycloakをインストール・設定してください。
