#!/usr/bin/env node

import { readFileSync, writeFileSync, existsSync, mkdirSync } from "fs";
import { spawn } from "child_process";
import path from "path";
import https from "https";
import http from "http";

const CONFIG = {
  // Backend OpenAPI endpoint
  BACKEND_URL: "http://localhost:8080",
  OPENAPI_ENDPOINT: "/api/docs/api-docs",

  // Output directories
  TEMP_DIR: "./temp",
  API_CLIENT_DIR: "./src/api",
  OPENAPI_SPEC_FILE: "./temp/openapi.json",

  // OpenAPI Generator configuration
  GENERATOR_NAME: "typescript-axios",
  PACKAGE_NAME: "api-client",
  PACKAGE_VERSION: "1.0.0",
};

function ensureDirectoryExists(dirPath) {
  if (!existsSync(dirPath)) {
    mkdirSync(dirPath, { recursive: true });
    console.log(`✅ Created directory: ${dirPath}`);
  }
}

async function fetchOpenApiSpec() {
  console.log("🔄 Fetching OpenAPI specification from backend...");

  const url = `${CONFIG.BACKEND_URL}${CONFIG.OPENAPI_ENDPOINT}`;

  return new Promise((resolve, reject) => {
    const urlObj = new URL(url);
    const client = urlObj.protocol === "https:" ? https : http;

    const request = client.get(url, (response) => {
      if (response.statusCode !== 200) {
        reject(
          new Error(`HTTP ${response.statusCode}: ${response.statusMessage}`)
        );
        return;
      }

      let data = "";
      response.on("data", (chunk) => {
        data += chunk;
      });

      response.on("end", () => {
        try {
          const openApiSpec = JSON.parse(data);

          ensureDirectoryExists(CONFIG.TEMP_DIR);

          writeFileSync(
            CONFIG.OPENAPI_SPEC_FILE,
            JSON.stringify(openApiSpec, null, 2)
          );
          console.log(
            `✅ OpenAPI specification saved to: ${CONFIG.OPENAPI_SPEC_FILE}`
          );

          resolve(openApiSpec);
        } catch (error) {
          reject(new Error(`Failed to parse JSON: ${error.message}`));
        }
      });
    });

    request.on("error", (error) => {
      reject(error);
    });

    request.setTimeout(10000, () => {
      request.destroy();
      reject(new Error("Request timeout"));
    });
  }).catch((error) => {
    console.error("❌ Failed to fetch OpenAPI specification:");
    console.error(`   URL: ${url}`);
    console.error(`   Error: ${error.message}`);
    console.error(
      "\n💡 Make sure your backend is running on http://localhost:8080"
    );
    process.exit(1);
  });
}

function runCommand(command, args = [], options = {}) {
  return new Promise((resolve, reject) => {
    console.log(`🔄 Running: ${command} ${args.join(" ")}`);

    const child = spawn(command, args, {
      stdio: "inherit",
      shell: true,
      ...options,
    });

    child.on("close", (code) => {
      if (code === 0) {
        resolve();
      } else {
        reject(new Error(`Command failed with exit code ${code}`));
      }
    });

    child.on("error", reject);
  });
}

async function generateApiClient() {
  console.log("🔄 Generating Axios API client...");

  ensureDirectoryExists(CONFIG.API_CLIENT_DIR);

  const args = [
    "generate",
    "-i",
    CONFIG.OPENAPI_SPEC_FILE,
    "-g",
    CONFIG.GENERATOR_NAME,
    "-o",
    CONFIG.API_CLIENT_DIR,
    "--additional-properties",
    [
      `npmName=${CONFIG.PACKAGE_NAME}`,
      `npmVersion=${CONFIG.PACKAGE_VERSION}`,
      "supportsES6=true",
      "withInterfaces=true",
      "withSeparateModelsAndApi=true",
      "apiPackage=api",
      "modelPackage=models",
      "withoutPrefixEnums=true",
      "enumPropertyNaming=original",
    ].join(","),
    "--skip-validate-spec",
  ];

  try {
    await runCommand("npx", ["@openapitools/openapi-generator-cli", ...args]);
    console.log(
      `✅ API client generated successfully in: ${CONFIG.API_CLIENT_DIR}`
    );
  } catch (error) {
    console.error("❌ Failed to generate API client:");
    console.error(`   Error: ${error.message}`);
    process.exit(1);
  }
}

async function main() {
  console.log("Starting API client update process...\n");

  try {
    await fetchOpenApiSpec();
    console.log("");

    await generateApiClient();
    console.log("");

    console.log("API client update completed successfully!");
  } catch (error) {
    console.error("API client update failed:");
    console.error(`   ${error.message}`);
    process.exit(1);
  }
}

main();
