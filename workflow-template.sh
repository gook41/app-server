#!/bin/bash

# Workflow Template Script (Shell version)
# 작업 절차: .prompt/*.md 읽고 업무 파악 -> 업무 진행 -> progress 업데이트

# Default parameters
ACTION="help"
TASK_TYPE=""
DRY_RUN=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -a|--action)
            ACTION="$2"
            shift 2
            ;;
        -t|--task-type)
            TASK_TYPE="$2"
            shift 2
            ;;
        -d|--dry-run)
            DRY_RUN=true
            shift
            ;;
        -h|--help)
            ACTION="help"
            shift
            ;;
        *)
            echo "Unknown option $1"
            ACTION="help"
            shift
            ;;
    esac
done

# Color output functions
write_color_output() {
    local message="$1"
    local color="$2"
    
    case $color in
        "Red")
            echo -e "\033[31m$message\033[0m"
            ;;
        "Green")
            echo -e "\033[32m$message\033[0m"
            ;;
        "Yellow")
            echo -e "\033[33m$message\033[0m"
            ;;
        "Cyan")
            echo -e "\033[36m$message\033[0m"
            ;;
        "Magenta")
            echo -e "\033[35m$message\033[0m"
            ;;
        "Gray")
            echo -e "\033[90m$message\033[0m"
            ;;
        *)
            echo "$message"
            ;;
    esac
}

# Read and validate .prompt files
read_prompt_files() {
    write_color_output "Reading .prompt files..." "Cyan"
    
    local prompt_dir="./.prompt"
    if [[ ! -d "$prompt_dir" ]]; then
        write_color_output "ERROR: .prompt directory not found." "Red"
        return 1
    fi
    
    local required_files=("progress-kanban.md" "progress-memory.md" "progress-roadmap.md" "read.md")
    
    for file in "${required_files[@]}"; do
        local file_path="$prompt_dir/$file"
        if [[ -f "$file_path" ]]; then
            write_color_output "  OK: $file" "Green"
        else
            write_color_output "  ERROR: $file not found." "Red"
            return 1
        fi
    done
    
    return 0
}

# Analyze current work status
analyze_current_work() {
    write_color_output "Analyzing current work status..." "Cyan"
    
    local current_task="Unknown"
    local progress="0"
    local last_time="00:00"
    
    # Extract current task from read.md
    if [[ -f "./.prompt/read.md" ]]; then
        local read_content=$(cat "./.prompt/read.md")
        if [[ $read_content =~ ###\ (.+) ]]; then
            current_task="${BASH_REMATCH[1]}"
            write_color_output "  Current Task: $current_task" "Yellow"
        fi
    fi
    
    # Extract progress from kanban
    if [[ -f "./.prompt/progress-kanban.md" ]]; then
        local kanban_content=$(cat "./.prompt/progress-kanban.md")
        if [[ $kanban_content =~ \(전체\ ([0-9]+)%\) ]]; then
            progress="${BASH_REMATCH[1]}"
            write_color_output "  Progress: $progress%" "Yellow"
        fi
    fi
    
    # Extract last time from memory
    if [[ -f "./.prompt/progress-memory.md" ]]; then
        local memory_content=$(cat "./.prompt/progress-memory.md")
        if [[ $memory_content =~ \(([0-9]{2}:[0-9]{2})\) ]]; then
            last_time="${BASH_REMATCH[1]}"
            write_color_output "  Last Time: $last_time" "Yellow"
        fi
    fi
    
    return 0
}

# Execute work based on task type
execute_work() {
    local task_type="$1"
    
    write_color_output "Executing work..." "Cyan"
    
    if [[ "$DRY_RUN" == true ]]; then
        write_color_output "  [DRY RUN] Simulation mode" "Magenta"
    fi
    
    case "${task_type,,}" in
        "domain")
            write_color_output "  Domain model expansion started" "Green"
            if [[ "$DRY_RUN" == false ]]; then
                write_color_output "    - Creating Inventory entity..." "Gray"
                write_color_output "    - Creating InboundOrder entity..." "Gray"
                write_color_output "    - Creating OutboundOrder entity..." "Gray"
                write_color_output "    - Creating Log entity..." "Gray"
            fi
            ;;
        "api")
            write_color_output "  API implementation started" "Green"
            if [[ "$DRY_RUN" == false ]]; then
                write_color_output "    - Implementing auth API..." "Gray"
                write_color_output "    - Implementing CRUD API..." "Gray"
            fi
            ;;
        "test")
            write_color_output "  Test writing started" "Green"
            if [[ "$DRY_RUN" == false ]]; then
                write_color_output "    - Writing unit tests..." "Gray"
                write_color_output "    - Writing integration tests..." "Gray"
            fi
            ;;
        *)
            write_color_output "  No specific task type. Using current priority task." "Yellow"
            write_color_output "    Current task: $task_type" "Gray"
            ;;
    esac
    
    return 0
}

# Update progress files
update_progress() {
    local task_type="$1"
    
    write_color_output "Updating progress..." "Cyan"
    
    local current_time=$(date +"%H:%M")
    local current_date=$(date +"%Y-%m-%d")
    local current_datetime=$(date +"%Y-%m-%d %H:%M")
    
    if [[ "$DRY_RUN" == true ]]; then
        write_color_output "  [DRY RUN] Would update files at: $current_date $current_time" "Magenta"
        write_color_output "    Task type: $task_type" "Gray"
    else
        # Update progress-memory.md with current session info
        local memory_path="./.prompt/progress-memory.md"
        if [[ -f "$memory_path" ]]; then
            # Update current session timestamp
            sed -i "s/## 현재 세션 상태 ([^)]*)/## 현재 세션 상태 ($current_datetime)/g" "$memory_path"
            
            # Add new work entry to session continuity
            local new_entry="- **마지막 작업**: $task_type 작업 진행 ($current_time)"
            sed -i "s/- \*\*마지막 작업\*\*:.*/$(echo "$new_entry" | sed 's/[[\.*^$()+?{|]/\\&/g')/g" "$memory_path"
            
            write_color_output "  Updated progress-memory.md" "Green"
        fi
        
        # Update progress-kanban.md with current date
        local kanban_path="./.prompt/progress-kanban.md"
        if [[ -f "$kanban_path" ]]; then
            # Update sprint status date
            sed -i "s/## 현재 스프린트 상태 ([^)]*)/## 현재 스프린트 상태 ($current_date)/g" "$kanban_path"
            
            write_color_output "  Updated progress-kanban.md" "Green"
        fi
        
        # Update read.md with current task info
        local read_path="./.prompt/read.md"
        if [[ -f "$read_path" ]]; then
            # Create temporary file with updated ACTION section
            local temp_file=$(mktemp)
            local in_action_section=false
            
            while IFS= read -r line; do
                if [[ "$line" == "# ACTION" ]]; then
                    echo "# ACTION" >> "$temp_file"
                    echo "" >> "$temp_file"
                    echo "### 현재 작업 $task_type" >> "$temp_file"
                    echo "- 작업 시작: $current_datetime" >> "$temp_file"
                    echo "- 작업 유형: $task_type" >> "$temp_file"
                    echo "- 상태: 진행 중" >> "$temp_file"
                    echo "" >> "$temp_file"
                    in_action_section=true
                elif [[ "$line" == "---" ]] && [[ "$in_action_section" == true ]]; then
                    echo "---" >> "$temp_file"
                    in_action_section=false
                elif [[ "$in_action_section" == false ]]; then
                    echo "$line" >> "$temp_file"
                fi
            done < "$read_path"
            
            mv "$temp_file" "$read_path"
            write_color_output "  Updated read.md" "Green"
        fi
    fi
    
    return 0
}

# Show help information
show_help() {
    write_color_output "Workflow Template Script Usage" "Cyan"
    echo ""
    write_color_output "Usage:" "White"
    write_color_output "  ./workflow-template.sh -a [action] [-t [type]] [-d]" "Gray"
    echo ""
    write_color_output "Actions:" "White"
    write_color_output "  analyze    - Read and analyze .prompt files" "Gray"
    write_color_output "  execute    - Execute work (requires task type)" "Gray"
    write_color_output "  update     - Update progress" "Gray"
    write_color_output "  workflow   - Full workflow (analyze -> execute -> update)" "Gray"
    echo ""
    write_color_output "Task Types:" "White"
    write_color_output "  domain     - Domain model expansion" "Gray"
    write_color_output "  api        - API implementation" "Gray"
    write_color_output "  test       - Test writing" "Gray"
    echo ""
    write_color_output "Options:" "White"
    write_color_output "  -d, --dry-run    - Simulation mode only" "Gray"
    echo ""
    write_color_output "Examples:" "White"
    write_color_output "  ./workflow-template.sh -a workflow -t domain -d" "Yellow"
    write_color_output "  ./workflow-template.sh -a analyze" "Yellow"
}

# Main function
main() {
    write_color_output "Workflow Template Script Started" "Green"
    write_color_output "Process: Read .prompt/*.md -> Understand work -> Execute -> Update progress" "White"
    echo ""
    
    case "${ACTION,,}" in
        "analyze")
            if read_prompt_files; then
                analyze_current_work
                write_color_output "Analysis completed" "Green"
            fi
            ;;
        "execute")
            if [[ -z "$TASK_TYPE" ]]; then
                write_color_output "ERROR: Task type required for execute action" "Red"
                return 1
            fi
            if read_prompt_files; then
                analyze_current_work
                execute_work "$TASK_TYPE"
            fi
            ;;
        "update")
            if read_prompt_files; then
                analyze_current_work
                update_progress "$TASK_TYPE"
            fi
            ;;
        "workflow")
            if read_prompt_files; then
                analyze_current_work
                if execute_work "$TASK_TYPE"; then
                    update_progress "$TASK_TYPE"
                    write_color_output "Full workflow completed!" "Green"
                fi
            fi
            ;;
        "help")
            show_help
            ;;
        *)
            write_color_output "Unknown action: $ACTION" "Red"
            show_help
            ;;
    esac
}

# Execute main function
main