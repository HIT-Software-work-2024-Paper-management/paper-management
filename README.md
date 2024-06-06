paper-management
├── frontend/                      # 前端代码
│   ├── public/                    # 公共资源
│   │   ├── index.html             # 主HTML文件
│   ├── src/                       # 源代码
│   │   ├── css/                   # 样式文件
│   │   │   └── styles.css         # 主样式文件
│   │   ├── js/                    # JavaScript 文件
│   │   │   └── app.js             # 主应用文件
│   ├── package.json               # 前端依赖配置（如果使用npm）
├── backend/                       # 后端代码
│   ├── src/                       # 源代码
│   │   ├── main/
│   │   │   ├── java/              # Java 源文件
│   │   │   │   └── com/
│   │   │   │       └── myproject/
│   │   │   │           ├── PaperManagementApplication.java # 主应用文件
│   │   │   │           ├── controller/    # 控制器
│   │   │   │           │   ├── PaperController.java
│   │   │   │           │   └── CategoryController.java
│   │   │   │           ├── model/         # 数据模型
│   │   │   │           │   ├── Paper.java
│   │   │   │           │   └── Category.java
│   │   │   │           ├── repository/    # 数据库操作
│   │   │   │           │   ├── PaperRepository.java
│   │   │   │           │   └── CategoryRepository.java
│   │   │   │           └── service/       # 服务层
│   │   │   │               ├── PaperService.java
│   │   │   │               └── CategoryService.java
│   │   ├── resources/
│   │   │   ├── application.properties     # 配置文件
│   │   ├── static/                        # 静态资源
│   │   └── templates/                     # 模板文件
│   ├── pom.xml                            # Maven 项目配置
├── paper-files/                           # 用于存储上传的论文文件
│   ├── example-paper1.pdf
│   ├── example-paper2.pdf
│   └── ...                                # 更多上传的论文文件
├── database/                              # 数据库相关文件
│   ├── migrations/                        # 数据库迁移
│   │   └── categories.sql                 # 分类表SQL文件
│   ├── seeds/                             # 数据库种子数据
├── docs/                                  # 文档
│   ├── README.md                          # 项目说明
│   ├── API.md                             # API 文档
│   ├── CONTRIBUTING.md                    # 贡献指南
├── .gitignore                             # Git忽略文件
├── LICENSE                                # 许可证
└── README.md                              # 项目说明


程序使用使用 Node.js 20.14 和 Express 框架来构建后端服务，并使用 mySQL作为数据库。
前端程序使用css、html和JavaScript来构建。后端使用Java语言来编写。


