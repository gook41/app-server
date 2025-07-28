# Workflow Template Script
# 작업 절차: .prompt/*.md 읽고 업무 파악 -> 업무 진행 -> progress 업데이트

param(
    [string]$Action = "help",
    [string]$TaskType = "",
    [switch]$DryRun = $false
)

function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

function Read-PromptFiles {
    Write-ColorOutput "Reading .prompt files..." "Cyan"
    
    $promptDir = ".\.prompt"
    if (-not (Test-Path $promptDir)) {
        Write-ColorOutput "ERROR: .prompt directory not found." "Red"
        return $false
    }
    
    $requiredFiles = @(
        "progress-kanban.md",
        "progress-memory.md", 
        "progress-roadmap.md",
        "read.md"
    )
    
    $analysis = @{}
    
    foreach ($file in $requiredFiles) {
        $filePath = Join-Path $promptDir $file
        if (Test-Path $filePath) {
            Write-ColorOutput "  OK: $file" "Green"
            $content = Get-Content $filePath -Raw -Encoding UTF8
            $analysis[$file] = $content
        } else {
            Write-ColorOutput "  ERROR: $file not found." "Red"
            return $false
        }
    }
    
    return $analysis
}

function Analyze-CurrentWork {
    param($Analysis)
    
    Write-ColorOutput "Analyzing current work status..." "Cyan"
    
    $currentTask = "Unknown"
    $progress = "0"
    $lastWork = "Unknown"
    $lastTime = "00:00"
    
    # Extract current task from read.md
    $readContent = $Analysis["read.md"]
    if ($readContent -match "### .+ (.+)") {
        $currentTask = $matches[1]
        Write-ColorOutput "  Current Task: $currentTask" "Yellow"
    }
    
    # Extract progress from kanban - look for percentage pattern
    $kanbanContent = $Analysis["progress-kanban.md"]
    if ($kanbanContent -match "\(전체 (\d+)%\)") {
        $progress = $matches[1]
        Write-ColorOutput "  Progress: $progress%" "Yellow"
    }
    
    # Extract last work from memory - look for time pattern
    $memoryContent = $Analysis["progress-memory.md"]
    if ($memoryContent -match "\((\d{2}:\d{2})\)") {
        $lastTime = $matches[1]
        Write-ColorOutput "  Last Time: $lastTime" "Yellow"
    }
    
    return @{
        CurrentTask = $currentTask
        Progress = $progress
        LastWork = $lastWork
        LastTime = $lastTime
    }
}

function Execute-Work {
    param($WorkInfo, $TaskType)
    
    Write-ColorOutput "Executing work..." "Cyan"
    
    if ($DryRun) {
        Write-ColorOutput "  [DRY RUN] Simulation mode" "Magenta"
    }
    
    switch ($TaskType.ToLower()) {
        "domain" {
            Write-ColorOutput "  Domain model expansion started" "Green"
            if (-not $DryRun) {
                Write-ColorOutput "    - Creating Inventory entity..." "Gray"
                Write-ColorOutput "    - Creating InboundOrder entity..." "Gray"
                Write-ColorOutput "    - Creating OutboundOrder entity..." "Gray"
                Write-ColorOutput "    - Creating Log entity..." "Gray"
            }
        }
        "api" {
            Write-ColorOutput "  API implementation started" "Green"
            if (-not $DryRun) {
                Write-ColorOutput "    - Implementing auth API..." "Gray"
                Write-ColorOutput "    - Implementing CRUD API..." "Gray"
            }
        }
        "test" {
            Write-ColorOutput "  Test writing started" "Green"
            if (-not $DryRun) {
                Write-ColorOutput "    - Writing unit tests..." "Gray"
                Write-ColorOutput "    - Writing integration tests..." "Gray"
            }
        }
        default {
            Write-ColorOutput "  No specific task type. Using current priority task." "Yellow"
            Write-ColorOutput "    Current task: $($WorkInfo.CurrentTask)" "Gray"
        }
    }
    
    return $true
}

function Update-Progress {
    param($WorkInfo, $TaskType)
    
    Write-ColorOutput "Updating progress..." "Cyan"
    
    $currentTime = Get-Date -Format "HH:mm"
    $currentDate = Get-Date -Format "yyyy-MM-dd"
    $currentDateTime = Get-Date -Format "yyyy-MM-dd HH:mm"
    
    if ($DryRun) {
        Write-ColorOutput "  [DRY RUN] Would update files at: $currentDate $currentTime" "Magenta"
        Write-ColorOutput "    Task type: $TaskType" "Gray"
    } else {
        # Update progress-memory.md with current session info
        $memoryPath = ".\.prompt\progress-memory.md"
        if (Test-Path $memoryPath) {
            $memoryContent = Get-Content $memoryPath -Raw -Encoding UTF8
            
            # Update current session timestamp
            $memoryContent = $memoryContent -replace "## 현재 세션 상태 \([\d\-\s:]+\)", "## 현재 세션 상태 ($currentDateTime)"
            
            # Add new work entry to session continuity
            $newEntry = "- **마지막 작업**: $TaskType 작업 진행 ($currentTime)"
            $memoryContent = $memoryContent -replace "- \*\*마지막 작업\*\*:.*", $newEntry
            
            [System.IO.File]::WriteAllText($memoryPath, $memoryContent, [System.Text.Encoding]::UTF8)
            Write-ColorOutput "  Updated progress-memory.md" "Green"
        }
        
        # Update progress-kanban.md with current date
        $kanbanPath = ".\.prompt\progress-kanban.md"
        if (Test-Path $kanbanPath) {
            $kanbanContent = Get-Content $kanbanPath -Raw -Encoding UTF8
            
            # Update sprint status date
            $kanbanContent = $kanbanContent -replace "## 현재 스프린트 상태 \([\d\-]+\)", "## 현재 스프린트 상태 ($currentDate)"
            
            [System.IO.File]::WriteAllText($kanbanPath, $kanbanContent, [System.Text.Encoding]::UTF8)
            Write-ColorOutput "  Updated progress-kanban.md" "Green"
        }
        
        # Update read.md with current task info
        $readPath = ".\.prompt\read.md"
        if (Test-Path $readPath) {
            $readContent = Get-Content $readPath -Raw -Encoding UTF8
            
            # Update ACTION section with current task
            $actionSection = @"
# ACTION

### 현재 작업 $TaskType
- 작업 시작: $currentDateTime
- 작업 유형: $TaskType
- 상태: 진행 중

---
"@
            # Replace the ACTION section including content until the next ---
            $readContent = $readContent -replace "# ACTION[\s\S]*?(?=---)", $actionSection
            
            [System.IO.File]::WriteAllText($readPath, $readContent, [System.Text.Encoding]::UTF8)
            Write-ColorOutput "  Updated read.md" "Green"
        }
    }
    
    return $true
}

function Show-Help {
    Write-ColorOutput "Workflow Template Script Usage" "Cyan"
    Write-ColorOutput ""
    Write-ColorOutput "Usage:" "White"
    Write-ColorOutput "  .\workflow-template.ps1 -Action [action] [-TaskType [type]] [-DryRun]" "Gray"
    Write-ColorOutput ""
    Write-ColorOutput "Actions:" "White"
    Write-ColorOutput "  analyze    - Read and analyze .prompt files" "Gray"
    Write-ColorOutput "  execute    - Execute work (requires TaskType)" "Gray"
    Write-ColorOutput "  update     - Update progress" "Gray"
    Write-ColorOutput "  workflow   - Full workflow (analyze -> execute -> update)" "Gray"
    Write-ColorOutput ""
    Write-ColorOutput "TaskTypes:" "White"
    Write-ColorOutput "  domain     - Domain model expansion" "Gray"
    Write-ColorOutput "  api        - API implementation" "Gray"
    Write-ColorOutput "  test       - Test writing" "Gray"
    Write-ColorOutput ""
    Write-ColorOutput "Options:" "White"
    Write-ColorOutput "  -DryRun    - Simulation mode only" "Gray"
    Write-ColorOutput ""
    Write-ColorOutput "Examples:" "White"
    Write-ColorOutput "  .\workflow-template.ps1 -Action workflow -TaskType domain -DryRun" "Yellow"
    Write-ColorOutput "  .\workflow-template.ps1 -Action analyze" "Yellow"
}

function Main {
    Write-ColorOutput "Workflow Template Script Started" "Green"
    Write-ColorOutput "Process: Read .prompt/*.md -> Understand work -> Execute -> Update progress" "White"
    Write-ColorOutput ""
    
    switch ($Action.ToLower()) {
        "analyze" {
            $analysis = Read-PromptFiles
            if ($analysis) {
                $workInfo = Analyze-CurrentWork $analysis
                Write-ColorOutput "Analysis completed" "Green"
            }
        }
        "execute" {
            if (-not $TaskType) {
                Write-ColorOutput "ERROR: TaskType required for execute action" "Red"
                return
            }
            $analysis = Read-PromptFiles
            if ($analysis) {
                $workInfo = Analyze-CurrentWork $analysis
                Execute-Work $workInfo $TaskType
            }
        }
        "update" {
            $analysis = Read-PromptFiles
            if ($analysis) {
                $workInfo = Analyze-CurrentWork $analysis
                Update-Progress $workInfo $TaskType
            }
        }
        "workflow" {
            $analysis = Read-PromptFiles
            if ($analysis) {
                $workInfo = Analyze-CurrentWork $analysis
                if (Execute-Work $workInfo $TaskType) {
                    Update-Progress $workInfo $TaskType
                    Write-ColorOutput "Full workflow completed!" "Green"
                }
            }
        }
        "help" {
            Show-Help
        }
        default {
            Write-ColorOutput "Unknown action: $Action" "Red"
            Show-Help
        }
    }
}

# Execute main function
Main